package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.Table;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Rappresenta la mappatura tra un bonifico extra SEPA e la relativa classe di elaborazione.<p>
 *
 * Questa classe definisce l'associazione tra un bonifico extra SEPA e un messaggio specifico,
 * oltre alla classe qualificata utilizzata per elaborare il messaggio.<p>
 *
 * La tabella associata nel database è {@code sotto_tipologia_bonifico_mappatura}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE sotto_tipologia_bonifico_mappatura (
 *     id UUID PRIMARY KEY,
 *     id_sotto_tipologia_bonifico UUID NOT NULL,
 *     id_messaggio VARCHAR(255) NOT NULL,
 *     mappatura_classe_qualificata VARCHAR(255) NOT NULL,
 *     CONSTRAINT fk_sotto_tipologia_bonifico FOREIGN KEY (id_sotto_tipologia_bonifico)
 *         REFERENCES sotto_tipologia_bonifico(id)
 * );
 * }</pre>
 *
 * @param id                          Identificativo univoco della mappatura.<p>
 * @param idSottoTipologiaBonifico    Identificativo della sotto-tipologia di bonifico extra SEPA associata.<p>
 * @param idMessaggio                 Identificativo del messaggio associato alla mappatura.<p>
 * @param mappaturaClasseQualificata  Nome della classe qualificata utilizzata per l'elaborazione.<p>
 */
@Table("sotto_tipologia_bonifico_mappatura")
public record SottoTipologiaBonificoMappatura(
        /// Identificativo univoco della mappatura.
        UUID id,

        /// Identificativo della sotto-tipologia di bonifico extra SEPA associata.
        @Column("id_sotto_tipologia_bonifico")
        UUID idSottoTipologiaBonifico,

        /// Identificativo del messaggio associato alla mappatura.
        @Column("id_messaggio")
        String idMessaggio,

        /// Nome della classe qualificata utilizzata per l'elaborazione.
        @Column("mappatura_classe_qualificata")
        String mappaturaClasseQualificata
) {

    /**
     * Entità che fornisce l'interfaccia tra la classe e il repository,
     * permettendo l'accesso ai dati.
     */
    public static class Entity implements com.flowpay.ccp.persistence.Entity<SottoTipologiaBonificoMappatura, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<SottoTipologiaBonificoMappatura> entityClass() {
            return SottoTipologiaBonificoMappatura.class;
        }
    }

    /**
     * Repository per la gestione dei dati di {@code SottoTipologiaBonificoMappatura}.<p>
     * Fornisce metodi per l'interazione con il database utilizzando un client SQL.<p>
     */
    public static class Repository extends com.flowpay.ccp.persistence.Repository<SottoTipologiaBonificoMappatura> {

        public Repository(SqlClient client, Function<Row, SottoTipologiaBonificoMappatura> decoder) {
            super(client, decoder);
        }

        /**
         * Restituisce tutte le mappature per un dato bonifico extra SEPA.
         *
         * @param creditTransferKindID Identificativo della sotto-tipologia di bonifico extra SEPA.
         * @return Un'istanza di {@code Multi<SottoTipologiaBonificoMappatura>} contenente i dati della mappatura.
         */
        public Multi<SottoTipologiaBonificoMappatura> getAllBySottoTipologiaBonifico(UUID creditTransferKindID) {
            return multi("""
            SELECT * FROM sotto_tipologia_bonifico_mappatura
            WHERE id_sotto_tipologia_bonifico = $1
            """, Tuple.of(creditTransferKindID));
        }
    }

    WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {

        SottoTipologiaBonifico.WithLinkedEntities sottoTipologiaBonifico;

        @Override
        public SottoTipologiaBonificoMappatura getEntity() {
            return SottoTipologiaBonificoMappatura.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            return Multi.createBy().merging().streams(multis);
        }

        @Override
        /// Non creiamo queste entità a DB
        public Uni<Void> insert(SqlClient sqlClient) {
            return Uni.createFrom().voidItem();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {

        }
    }
}