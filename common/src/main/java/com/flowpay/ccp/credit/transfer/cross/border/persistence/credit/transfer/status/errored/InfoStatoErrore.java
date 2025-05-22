package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.status.errored;

import com.flowpay.ccp.credit.transfer.cross.border.errors.ErrorCodes;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.persistence.*;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

/**
 * Rappresenta le informazioni relative allo stato di errore di un bonifico extra SEPA.<p>
 *
 * Questa classe memorizza i dettagli relativi agli errori che possono verificarsi
 * durante l'elaborazione di una transazione di bonifico extra SEPA, inclusi il codice
 * dell'errore, una descrizione e il timestamp di registrazione.<p>
 *
 * La tabella associata nel database è {@code bonifico_extra_sepa_info_stato_errore}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE bonifico_extra_sepa_info_stato_errore (
 *     id UUID PRIMARY KEY,
 *     id_bonifico_extra_sepa UUID NOT NULL,
 *     codice_errore VARCHAR(255),
 *     descrizione_errore TEXT,
 *     created_at TIMESTAMP WITH TIME ZONE NOT NULL,
 *     CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa) REFERENCES bonifico_extra_sepa(id)
 * );
 * }</pre>
 *
 * @param id                   Identificativo univoco del record.<p>
 * @param idBonificoExtraSepa   Identificativo del bonifico extra SEPA associato.<p>
 * @param codiceErrore         Codice dell'errore associato alla transazione.<p>
 * @param descrizioneErrore    Descrizione dettagliata dell'errore.<p>
 * @param createdAt            Timestamp della creazione della registrazione dell'errore.<p>
 *
 */
@Table("bonifico_extra_sepa_info_stato_errore")
public record InfoStatoErrore(
        UUID id,

        @Column("id_bonifico_extra_sepa")
        UUID idBonificoExtraSepa,

        /// Codice dell'errore associato alla transazione.
        @Column("codice_errore")
        String codiceErrore,

        /// Descrizione dettagliata dell'errore.
        @Column("descrizione_errore")
        String descrizioneErrore,

        /// Timestamp della creazione della registrazione dell'errore.
        @Column("created_at")
        @CreationTimeStamp
        Instant createdAt
) {

    /**
     * Salva un errore associato a un bonifico extra SEPA nel database.<p>
     *
     * @param creditTransfer Il bonifico extra SEPA che ha generato l'errore.<p>
     * @param pool           Il client del database PostgreSQL.<p>
     * @param exception      L'eccezione da registrare come errore.<p>
     * @return Uni<Void> indicando il completamento dell'operazione.<p>
     */
    public static Uni<Void> storeError(BonificoExtraSepa creditTransfer, SqlClient pool, Throwable exception) {
        var errorEntity = new InfoStatoErrore.Entity();
        var errorRepository = errorEntity.repository(pool);

        var errorCode = exception instanceof ErrorCodes codes ? codes.code.name() : "UNKNOWN";

        return errorRepository.run(errorEntity.insert(
                new InfoStatoErrore(
                        UUID.randomUUID(),
                        creditTransfer.id(),
                        errorCode,
                        exception.getMessage(),
                        null
                )));
    }

    /**
     * Restituisce una funzione che registra un errore associato a un bonifico extra SEPA.<p>
     *
     * @param creditTransfer Il bonifico extra SEPA.<p>
     * @param pool           Il client del database PostgreSQL.<p>
     * @return Function per salvare un errore come Uni<Void>.<p>
     */
    public static Function<Throwable, Uni<Void>> storeError(BonificoExtraSepa creditTransfer, PgPool pool) {
        return exception -> storeError(creditTransfer, pool, exception);
    }

    /**
     * Classe che rappresenta l'entità e fornisce il repository associato.
     */
    public static final class Entity implements com.flowpay.ccp.persistence.Entity<InfoStatoErrore, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<InfoStatoErrore> entityClass() {
            return InfoStatoErrore.class;
        }
    }

    /**
     * Repository per l'accesso ai dati della tabella {@code bonifico_extra_sepa_info_stato_errore}.
     */
    public static final class Repository extends com.flowpay.ccp.persistence.Repository<InfoStatoErrore> {

        public Repository(SqlClient client, Function<Row, InfoStatoErrore> decoder) {
            super(client, decoder);
        }

        /**
         * Recupera lo stato di errore di un bonifico extra SEPA in base al suo ID.<p>
         *
         * @param id Identificativo del bonifico extra SEPA.<p>
         * @return Uni contenente le informazioni sullo stato di errore.<p>
         */
        public Uni<InfoStatoErrore> getByCreditTransferID(UUID id) {
            return singleOrNull(
                    "SELECT * FROM bonifico_extra_sepa_info_stato_errore WHERE id_bonifico_extra_sepa = $1",
                    Tuple.of(id)
            );
        }
    }
}