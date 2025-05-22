package com.flowpay.ccp.credit.transfer.cross.border.persistence.verify;

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
 * CREATE TABLE dati_verifica_bonifico_errore_tecnico (
 *     id UUID PRIMARY KEY,
 *     id_dati_verifica_bonifico UUID NOT NULL,
 *     codice VARCHAR(255) NOT NULL,
 *     descrizione VARCHAR(255) NOT NULL
 * 
 *     CONSTRAINT fk_dati_verifica_bonifico FOREIGN KEY (id_dati_verifica_bonifico)
 *         REFERENCES dati_verifica_bonifico(id)
 * );
 * }</pre>
 * 
 * @see DatiVerificaBonifico
 */
@Table("dati_verifica_bonifico_errore_tecnico")
public record DatiVerificaBonificoErroreTecnico(
     /** Identificativo univoco del record. */
    UUID id,

    /** Identificativo del processo di verifica */
    @Column("id_dati_verifica_bonifico")
    UUID idDatiVerificaBonifico,

    /** Codice identificativo dell'avvertenza */
    @Column("codice")
    String codice,

    /** Descrizione dell'avvertenza */
    @Column("descrizione")
    String descrizione
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<DatiVerificaBonificoErroreTecnico, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<DatiVerificaBonificoErroreTecnico> entityClass() {
            return DatiVerificaBonificoErroreTecnico.class;
        }

    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<DatiVerificaBonificoErroreTecnico> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, DatiVerificaBonificoErroreTecnico> decoder) {
            super(client, decoder);
        }

        public Multi<DatiVerificaBonificoErroreTecnico> getAllByDatiVerificaBonifico(UUID idDatiVerificaBonifico) {
            return multi("""
                SELECT * FROM dati_verifica_bonifico_errore_tecnico 
                WHERE id_dati_verifica_bonifico = $1
            """, Tuple.of(idDatiVerificaBonifico));
        }
    }


    /**
     * Prepara a recuperare le entity linkate.
     */
    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {
        public DatiVerificaBonifico.WithLinkedEntities datiVerificaBonifico;
        
        @Override
        public DatiVerificaBonificoErroreTecnico getEntity() {
            return DatiVerificaBonificoErroreTecnico.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(datiVerificaBonifico);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.datiVerificaBonifico == null) {
                multis.add(new DatiVerificaBonifico.Entity()
                        .repository(sqlClient).getById(idDatiVerificaBonifico())
                        .map(linked -> {
                            this.datiVerificaBonifico = linked.withLinkedEntities();
                            return this.datiVerificaBonifico;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.datiVerificaBonifico));
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
