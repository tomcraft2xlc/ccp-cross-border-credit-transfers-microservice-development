package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.status.inserted;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.status.InsertedOutcomeCode;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.CreationTimeStamp;
import com.flowpay.ccp.persistence.EnumKind;
import com.flowpay.ccp.persistence.Table;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

/**
 * Rappresenta le informazioni relative allo stato "Inserito" di un bonifico extra SEPA.<p>
 *
 * Questa classe memorizza i dettagli associati alla fase di inserimento
 * di una transazione di bonifico extra SEPA, inclusi l'esito dell'inserimento,
 * il codice del risultato, l'ID della richiesta e altre informazioni di stato.<p>
 *
 * La tabella associata nel database è {@code bonifico_extra_sepa_info_stato_inserito}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE bonifico_extra_sepa_info_stato_inserito (
 *     id UUID PRIMARY KEY,
 *     id_bonifico_extra_sepa UUID NOT NULL,
 *     outcome VARCHAR(255),
 *     codice_risultato VARCHAR(255),
 *     id_richiesta INTEGER,
 *     disponibile BOOLEAN,
 *     saldo NUMERIC(18,2),
 *     embargo BOOLEAN,
 *     created_at TIMESTAMP WITH TIME ZONE NOT NULL,
 *     CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa) REFERENCES bonifico_extra_sepa(id)
 * );
 * }</pre>
 *
 * @param id                  Identificativo univoco del record.<p>
 * @param idBonificoExtraSepa  Identificativo del bonifico extra SEPA associato.<p>
 * @param outcome             Esito dell'inserimento della transazione.<p>
 * @param codiceRisultato      Codice del risultato dell'inserimento.<p>
 * @param idRichiesta         Identificativo della richiesta di bonifico.<p>
 * @param disponibile         Indica se i fondi sono disponibili per la transazione.<p>
 * @param saldo               Saldo disponibile per la transazione.<p>
 * @param embargo             Indica se la transazione è soggetta a embargo.<p>
 * @param createdAt           Timestamp della creazione dell'entità nel database.<p>
 *
 * @see InsertedOutcomeCode
 */
@Table("bonifico_extra_sepa_info_stato_inserito")
public record InfoStatoInserito(
        UUID id,

        @Column("id_bonifico_extra_sepa")
        UUID idBonificoExtraSepa,

        /// Esito dell'inserimento della transazione.
        @Column("outcome")
        String outcome,

        /// Codice del risultato dell'inserimento.
        @Column("codice_risultato")
        @EnumKind
        InsertedOutcomeCode codiceRisultato,

        /// Identificativo della richiesta di bonifico.
        @Column("id_richiesta")
        Integer idRichiesta,

        /// Indica se i fondi sono disponibili per la transazione.
        Boolean disponibile,

        /// Saldo disponibile per la transazione.
        BigDecimal saldo,

        /// Indica se la transazione è soggetta a embargo.
        Boolean embargo,

        /// Timestamp della creazione dell'entità nel database.
        @CreationTimeStamp
        @Column("created_at")
        Instant createdAt
) {
    /**
     * Costruttore per creare un'istanza da un bonifico extra SEPA e la risposta di inserimento.<p>
     *
     * @param creditTransfer L'oggetto bonifico extra SEPA.<p>
     * @param reply          La risposta dell'inserimento della transazione.<p>
     */
    /*
    public InfoStatoInserito(
            BonificoExtraSepa creditTransfer,
            InsertCreditTransferReply reply
    ) {
        this(
                UUID.randomUUID(),
                creditTransfer.id(),
                reply.esito(),
                reply.codiceEsito(),
                reply.idRichiesta(),
                reply.flagDisponibilita(),
                reply.saldoRapporto(),
                reply.flagEmbargo(),
                null
        );
    } */

    /**
     * Gestisce l'entità e fornisce il repository associato.
     */
    public static final class Entity implements com.flowpay.ccp.persistence.Entity<InfoStatoInserito, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<InfoStatoInserito> entityClass() {
            return InfoStatoInserito.class;
        }
    }

    /**
     * Repository per l'accesso ai dati della tabella {@code bonifico_extra_sepa_info_stato_inserito}.
     */
    public static final class Repository extends com.flowpay.ccp.persistence.Repository<InfoStatoInserito> {

        public Repository(SqlClient client, Function<Row, InfoStatoInserito> decoder) {
            super(client, decoder);
        }

        /**
         * Recupera lo stato di inserimento di un bonifico extra SEPA in base al suo ID.<p>
         *
         * @param id Identificativo del bonifico extra SEPA.<p>
         * @return Uni contenente lo stato di inserimento della transazione.<p>
         */
        public Uni<InfoStatoInserito> getByCreditTransferID(UUID id) {
            return singleOrNull("""
            SELECT * FROM bonifico_extra_sepa_info_stato_inserito
            WHERE id_bonifico_extra_sepa = $1
            """, Tuple.of(id));
        }
    }
}