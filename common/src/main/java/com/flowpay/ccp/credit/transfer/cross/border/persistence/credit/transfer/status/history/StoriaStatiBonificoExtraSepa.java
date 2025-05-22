package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.status.history;

import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;
import com.flowpay.ccp.persistence.*;
import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

/**
 * Rappresenta la cronologia degli stati di un bonifico extra SEPA.<p>
 *
 * Questa classe tiene traccia delle modifiche di stato associate a un bonifico extra SEPA,
 * registrando lo stato attuale, il nuovo stato, eventuali note e la data di creazione.<p>
 *
 * La tabella associata nel database è {@code storia_stati_bonifico_extra_sepa}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE storia_stati_bonifico_extra_sepa (
 *     id UUID PRIMARY KEY,
 *     id_bonifico_extra_sepa UUID NOT NULL,
 *     stato_attuale VARCHAR(255) NOT NULL,
 *     nuovo_stato VARCHAR(255) NOT NULL,
 *     note TEXT,
 *     created_at TIMESTAMP WITH TIME ZONE NOT NULL,
 *     CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa) REFERENCES bonifico_extra_sepa(id)
 * );
 * }</pre>
 *
 * @param id                Identificativo univoco del record.<p>
 * @param idBonificoExtraSepa  Identificativo del bonifico extra SEPA associato.<p>
 * @param statoAttuale      Stato attuale del bonifico extra SEPA.<p>
 * @param nuovoStato        Nuovo stato in cui è stato aggiornato il bonifico.<p>
 * @param note              Eventuali note aggiuntive relative alla modifica dello stato.<p>
 * @param createdAt         Timestamp della creazione della registrazione della cronologia.<p>
 *
 * @see CreditTransferStatus
 */
@Table("storia_stati_bonifico_extra_sepa")
public record StoriaStatiBonificoExtraSepa(
        UUID id,

        @Column("id_bonifico_extra_sepa")
        UUID idBonificoExtraSepa,

        /// Stato attuale del bonifico extra SEPA.
        @Column("stato_attuale")
        String statoAttuale,

        /// Nuovo stato in cui è stato aggiornato il bonifico.
        @Column("nuovo_stato")
        String nuovoStato,

        /// Eventuali note aggiuntive relative alla modifica dello stato.
        String note,

        /// Timestamp della creazione della registrazione della cronologia.
        @Column("created_at")
        @CreationTimeStamp
        Instant createdAt
) {

    /**
     * Classe che rappresenta l'entità e fornisce il repository associato.
     */
    public static class Entity implements com.flowpay.ccp.persistence.Entity<StoriaStatiBonificoExtraSepa, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<StoriaStatiBonificoExtraSepa> entityClass() {
            return StoriaStatiBonificoExtraSepa.class;
        }
    }

    /**
     * Repository per l'accesso ai dati della tabella {@code storia_stati_bonifico_extra_sepa}.
     */
    public static class Repository extends com.flowpay.ccp.persistence.Repository<StoriaStatiBonificoExtraSepa> {

        public Repository(SqlClient client, Function<Row, StoriaStatiBonificoExtraSepa> decoder) {
            super(client, decoder);
        }

        /**
         * Recupera la cronologia degli stati di un bonifico extra SEPA in base al suo ID.<p>
         *
         * @param creditTransferID Identificativo del bonifico extra SEPA.<p>
         * @return Multi contenente la cronologia degli stati.<p>
         */
        public Multi<StoriaStatiBonificoExtraSepa> findByCreditTransferID(UUID creditTransferID) {
            return this.multi("""
                SELECT * FROM storia_stati_bonifico_extra_sepa
                WHERE id_bonifico_extra_sepa = $1
                ORDER BY created_at DESC
                """, Tuple.of(creditTransferID));
        }
    }
}