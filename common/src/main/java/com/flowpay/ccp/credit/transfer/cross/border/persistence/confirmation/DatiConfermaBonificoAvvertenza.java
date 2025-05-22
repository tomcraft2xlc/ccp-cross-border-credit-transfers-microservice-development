package com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.Table;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

/**
 * Rappresenta le avvertenze relative ad un bonifico extra-SEPA.
 * 
 * SQL per la creazione della tabella:
 * <pre>{@code
 * CREATE TABLE dati_conferma_bonifico_avvertenze (
 *     id UUID PRIMARY KEY,
 *     id_dati_conferma_bonifico UUID NOT NULL,
 *     codice VARCHAR(255) NOT NULL,
 *     descrizione VARCHAR(255) NOT NULL
 * 
 *     CONSTRAINT fk_dati_conferma_bonifico FOREIGN KEY (id_dati_conferma_bonifico)
 *         REFERENCES dati_conferma_bonifico(id)
 * );
 * }</pre>
 * 
 * @see DatiConfermaBonifico
 */
@Table("dati_conferma_bonifico_avvertenze")
public record DatiConfermaBonificoAvvertenza(
     /** Identificativo univoco del record. */
    UUID id,

    /** Identificativo del processo di conferma */
    @Column("id_dati_conferma_bonifico")
    UUID idDatiConfermaBonifico,

    /** Codice identificativo dell'avvertenza */
    @Column("codice")
    String codice,

    /** Descrizione dell'avvertenza */
    @Column("descrizione")
    String descrizione
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<DatiConfermaBonificoAvvertenza, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<DatiConfermaBonificoAvvertenza> entityClass() {
            return DatiConfermaBonificoAvvertenza.class;
        }

    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<DatiConfermaBonificoAvvertenza> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, DatiConfermaBonificoAvvertenza> decoder) {
            super(client, decoder);
        }

        public Multi<DatiConfermaBonificoAvvertenza> getAllByDatiConfermaBonifico(UUID idDatiConfermaBonifico) {
            return multi("""
                SELECT * FROM dati_conferma_bonifico_avvertenze 
                WHERE id_dati_conferma_bonifico = $1
            """, Tuple.of(idDatiConfermaBonifico));
        }
    }


    /**
     * Prepara a recuperare le entity linkate.
     */
    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {
        public DatiConfermaBonifico.WithLinkedEntities datiConfermaBonifico;
        
        @Override
        public DatiConfermaBonificoAvvertenza getEntity() {
            return DatiConfermaBonificoAvvertenza.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(datiConfermaBonifico);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.datiConfermaBonifico == null) {
                multis.add(new DatiConfermaBonifico.Entity()
                        .repository(sqlClient).getById(idDatiConfermaBonifico())
                        .map(linked -> {
                            this.datiConfermaBonifico = linked.withLinkedEntities();
                            return this.datiConfermaBonifico;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.datiConfermaBonifico));
            }

            return Multi.createBy().merging().streams(multis);
        }

        @Override
        public Uni<Void> insert(SqlClient sqlClient) {
            var entity = new Entity();
            return entity.repository(sqlClient).run(entity.insert(getEntity()));
        }
    }
}
