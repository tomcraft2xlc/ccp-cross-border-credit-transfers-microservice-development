package com.flowpay.ccp.credit.transfer.cross.border.persistence;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.logging.Logger;

import com.google.common.collect.Streams;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Pool;
import io.vertx.mutiny.sqlclient.SqlClient;

public interface EntityWithLinkedEntities {
    Object getEntity();
    UUID id();

    /**
     * Load all descendant entities.
     * 
     * <pre>{@code
     * entity.withLinkedEntities()
     *     .loadAll(client)
     *     .onItem().ignoreAsUni()
     * }</pre>
     * 
     * <h2>Throtthling</h2>
     * 
     * The entities are loaded asyncroniusly, and each one will result in a query to the DB.
     * To avoid bashing the DB, the system was implemented with backpressure in mind. To throttle the
     * requests down <a href="https://smallrye.io/smallrye-mutiny/latest/guides/delaying-events/#throttling-a-multi">see the mutiny docs</a>.
     * 
     * @return A Multi that emit a item each time a descendent is loaded. Can be throttled to limit db usage.
     */
    default Multi<EntityWithLinkedEntities> loadAll(SqlClient sqlClient) {
        return loadAll(sqlClient, entity -> true);
    }
    /**
     * Load all descendant entities matching a predicate.
     * 
     * <pre>{@code
     * entity.withLinkedEntities()
     *     .loadAll(client, item -> myItemCheck(item))
     *     .onItem().ignoreAsUni()
     * }</pre>
     * 
     * <h2>Throtthling</h2>
     * 
     * The entities are loaded asyncroniusly, and each one will result in a query to the DB.
     * To avoid bashing the DB, the system was implemented with backpressure in mind. To throttle the
     * requests down <a href="https://smallrye.io/smallrye-mutiny/latest/guides/delaying-events/#throttling-a-multi">see the mutiny docs</a>.
     * 
     * @param deepen A predicate to decide if to recurse inside a given entity
     * @return A Multi that emit a item each time a descendent is loaded. Items can come down more than once, as they are rediscovered in the entity web.
     */
    default Multi<EntityWithLinkedEntities> loadAll(SqlClient sqlClient, Predicate<EntityWithLinkedEntities> deepen) {
        class SkipLoaded implements Predicate<EntityWithLinkedEntities> {
             Set<UUID> loaded;   
             Predicate<EntityWithLinkedEntities> inner;

             @Override
             public boolean test(EntityWithLinkedEntities t) {
                 if (loaded.contains(t.id())) {
                     return false;
                 }
                 return inner.test(t);
             }
        }

        return loadChilds(sqlClient)
            .onItem().transformToMultiAndMerge(child -> {
                if (!deepen.test(child)) {
                    // Test failed, do not deepen the child
                    return Multi.createFrom().item(child);
                }

                // Create a predicate that is equal to deepen,
                // but also avoid to reload child of already loaded entities
                var childDeepen = new SkipLoaded();
                if (deepen instanceof SkipLoaded skipLoadedDeepen) {
                    childDeepen.loaded = Streams.concat(
                        skipLoadedDeepen.loaded.stream(),
                        Stream.of(child.id())
                    ).collect(Collectors.toUnmodifiableSet());
                    childDeepen.inner = skipLoadedDeepen.inner;
                } else {
                    childDeepen.loaded = Set.of(child.id());
                    childDeepen.inner = deepen;
                }

                return Multi.createFrom().item(child).onCompletion().switchTo(child.loadAll(sqlClient, childDeepen));
            });
    }

    /**
     * Load all child entity.
     * 
     * This must NOT recurse inside the children, as that will be done automatically
     * 
     * @return A Multi with all loaded childs. Can be throttled to limit db usage.
     */
    Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient);

    /**
     * Insert just the main entity.
     * 
     * @return a Uni that completes as soon as the entity is completed
     */
    Uni<Void> insert(SqlClient sqlClient);

    /**
     * Collect all loaded linked entities.
     * 
     * @param consumer A consumer that will be called for each linked entity. Calling order is uninfluent. Calling with null is idempotent.
     */
    void collectLinked(Consumer<EntityWithLinkedEntities> consumer);



    /**
     * Insert all linked entities into the database.
     * 
     * This method will insert all linked entities, and all linked entities of the linked entities, and so on.
     * 
     * All entities will be inserted in a single transaction, 
     * and all constraints will be deferred until the end of the transaction.
     * 
     * This method will throw an exception if any of the entities already exist. 
     * Moreover it won't throw an exception if an entity is missing, aka is considering every
     * relation as either one-to-many or one-to-zero-or-one.
     * 
     * @param sqlClient The database to insert into
     * @return a Uni that completes when all entities have been inserted
     */
    default Uni<Void> insertAll(Pool sqlClient) {
        final Logger LOG = Logger.getLogger(getClass());

        LOG.debugf("Starting inserting root");
        // Open a transaction, and insert everything inside it
        // This ensures the database can't be observed in a inconsistent state
        return sqlClient.withTransaction(
            /// Set all constraint as deferred until the end of the transaction.
            /// This avoids having to insert the items in the right order, and
            /// enable us to just dump all items at once.
            /// Eventual constraint violations will be detected at the end of the transaction.
            transaction -> transaction.preparedQuery("SET CONSTRAINTS ALL DEFERRED").execute().flatMap(v -> {
                Set<UUID> seen = new HashSet<>();
                Deque<EntityWithLinkedEntities> parents = new ArrayDeque<>(List.of(this));
                List<Uni<Void>> insertions = new ArrayList<>();

                while (!parents.isEmpty()) {
                    var item = parents.pop();
                    if (seen.contains(item.id())) {
                        continue;
                    }
                    seen.add(item.id());

                    LOG.debugf("Inserting entity %s of class %s", item.id(), item.getEntity().getClass());

                    insertions.add(item.insert(transaction));

                    item.collectLinked(child -> {
                        if (child != null)
                            parents.push(child); // lmao
                    });
                }

                return Uni.combine().all().unis(insertions).discardItems();
            }));
    }
}