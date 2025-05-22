package com.flowpay.ccp.credit.transfer.cross.border.persistence.channel;

import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.Table;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.UUID;
import java.util.function.Function;

/**
 * Rappresenta un canale attraverso il quale possono essere effettuati pagamenti o bonifici.<p>
 *
 * Questa classe è mappata sulla tabella {@code canale} nel database e memorizza informazioni
 * relative all'identificativo del canale e alla necessità di autenticazione utente.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE canale (
 *     id UUID PRIMARY KEY,
 *     id_canale VARCHAR(255) NOT NULL,
 *     utente_richiesto BOOLEAN NOT NULL
 * );
 * }</pre>
 *
 * @param id               Identificativo univoco del canale.<p>
 * @param idCanale         Identificativo specifico del canale.<p>
 * @param utenteRichiesto  Indica se il canale richiede autenticazione utente.<p>
 */
@Table("canale")
public record Canale(
        /// Identificativo univoco del canale
        UUID id,

        /// Identificativo specifico del canale
        @Column("id_canale")
        String idCanale,

        /// Indica se il canale richiede autenticazione utente
        @Column("utente_richiesto")
        Boolean utenteRichiesto
) {

    /**
     * Classe che rappresenta l'entità di persistenza per il canale.
     */
    public static final class Entity implements com.flowpay.ccp.persistence.Entity<Canale, Repository> {

        /**
         * Restituisce il repository associato all'entità.
         *
         * @param sqlClient Il client SQL da utilizzare per la connessione al database.
         * @return Il repository per la gestione dei dati del canale.
         */
        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        /**
         * Restituisce la classe dell'entità associata.
         *
         * @return La classe {@code Canale}.
         */
        @Override
        public Class<Canale> entityClass() {
            return Canale.class;
        }
    }

    /**
     * Repository per la gestione dei dati del canale.<p>
     *
     * Fornisce metodi per recuperare i dettagli dei canali dal database.
     */
    public static final class Repository extends com.flowpay.ccp.persistence.Repository<Canale> {

        /**
         * Costruttore del repository.
         *
         * @param client  Il client SQL da utilizzare per l'accesso ai dati.
         * @param decoder Funzione di decodifica per trasformare una riga del database in un oggetto {@code Canale}.
         */
        public Repository(SqlClient client, Function<Row, Canale> decoder) {
            super(client, decoder);
        }

        /**
         * Recupera un canale tramite il suo ID univoco.
         *
         * @param id Identificativo del canale da cercare.
         * @return Un'istanza {@code Uni<Canale>} contenente i dati del canale, se trovato.
         */
        public Uni<Canale> getByID(UUID id) {
            return single("SELECT * FROM canale WHERE id = $1", Tuple.of(id));
        }

        /**
         * Recupera un canale tramite il suo identificativo specifico.
         *
         * @param channelID Identificativo del canale.
         * @return Un'istanza {@code Uni<Canale>} contenente i dati del canale, se trovato.
         */
        public Uni<Canale> getByChannelID(String channelID) {
            return single("SELECT * FROM canale WHERE id_canale = $1", Tuple.of(channelID));
        }
    }
}