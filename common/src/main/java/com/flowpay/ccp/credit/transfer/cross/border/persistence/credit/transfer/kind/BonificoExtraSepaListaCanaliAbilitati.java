package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind;

import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.channel.Canale;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.Table;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Rappresenta l'associazione tra un canale e un sotto-tipo di bonifico extra SEPA,
 * indicando se un canale è abilitato o meno a creare un bonifico di un determinato tipo.<p>
 *
 * Questa classe è mappata sulla tabella {@code bonifico_extra_sepa_lista_canali_abilitati} nel database.<p>
 *
 * Riferimenti esterni:<p>
 * <ul>
 *   <li>{@link SottoTipologiaBonifico} - Identifica il sotto-tipo di bonifico extra SEPA associato.</li>
 *   <li>{@link com.flowpay.ccp.credit.transfer.cross.border.persistence.channel.Canale} - Indica il canale su cui il bonifico può essere effettuato.</li>
 * </ul>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE bonifico_extra_sepa_lista_canali_abilitati (
 *     id UUID PRIMARY KEY,
 *     id_sottotipo_bonifico_extra_sepa UUID NOT NULL,
 *     id_canale UUID NOT NULL,
 *     CONSTRAINT fk_sottotipo_bonifico FOREIGN KEY (id_sottotipo_bonifico_extra_sepa)
 *         REFERENCES sotto_tipologia_bonifico(id),
 *     CONSTRAINT fk_canale FOREIGN KEY (id_canale)
 *         REFERENCES canale(id)
 * );
 * }</pre>
 *
 * @param id                              Identificativo univoco del record.<p>
 * @param idSottotipoBonificoExtraSepa    Identificativo del sotto-tipo di bonifico extra SEPA.<p>
 * @param idCanale                        Identificativo del canale abilitato.<p>
 *
 * @see SottoTipologiaBonifico
 * @see com.flowpay.ccp.credit.transfer.cross.border.persistence.channel.Canale
 */
@Table("bonifico_extra_sepa_lista_canali_abilitati")
public record BonificoExtraSepaListaCanaliAbilitati(

        /// Identificativo univoco del record.
        UUID id,

        /// Identificativo del sotto-tipo di bonifico extra SEPA.
        /// <p>Mappato in `id_sottotipo_bonifico_extra_sepa`.</p>
        @Column("id_sottotipo_bonifico_extra_sepa")
        UUID idSottotipoBonificoExtraSepa,

        /// Identificativo del canale abilitato.
        /// <p>Mappato in `id_canale`.</p>
        @Column("id_canale")
        UUID idCanale,

        @Column("stato_default")
        CreditTransferStatus statoDefault
) {

    /**
     * Entità che gestisce l'interfaccia tra la classe e il repository,
     * fornendo metodi per l'accesso ai dati.
     */
    public static class Entity implements com.flowpay.ccp.persistence.Entity<BonificoExtraSepaListaCanaliAbilitati, Repository> {

        /**
         * Restituisce un'istanza del repository associato all'entità.
         *
         * @param sqlClient Il client SQL da utilizzare per la connessione al database.
         * @return Il repository per la gestione dei dati di {@code BonificoExtraSepaListaCanaliAbilitati}.
         */
        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        /**
         * Restituisce la classe dell'entità associata.
         *
         * @return La classe {@code BonificoExtraSepaListaCanaliAbilitati}.
         */
        @Override
        public Class<BonificoExtraSepaListaCanaliAbilitati> entityClass() {
            return BonificoExtraSepaListaCanaliAbilitati.class;
        }
    }

    /**
     * Repository per la gestione dei dati di {@code BonificoExtraSepaListaCanaliAbilitati}.<p>
     *
     * Questa classe fornisce metodi per l'interazione con il database, utilizzando un client SQL.<p>
     */
    public static class Repository extends com.flowpay.ccp.persistence.Repository<BonificoExtraSepaListaCanaliAbilitati> {

        /**
         * Costruttore per il repository.
         *
         * @param client  Il client SQL da utilizzare per l'accesso ai dati.
         * @param decoder Funzione di decodifica per trasformare una riga del database in un oggetto {@code BonificoExtraSepaListaCanaliAbilitati}.
         */
        public Repository(SqlClient client, java.util.function.Function<io.vertx.mutiny.sqlclient.Row, BonificoExtraSepaListaCanaliAbilitati> decoder) {
            super(client, decoder);
        }

        public Uni<BonificoExtraSepaListaCanaliAbilitati> getBySottoTipologiaBonificoAndCanale(UUID idSottotipologiaBonifico, UUID idCanale) {
            return single("""
            SELECT * FROM bonifico_extra_sepa_lista_canali_abilitati
            WHERE
                id_sottotipo_bonifico_extra_sepa = $1 AND
                id_canale = $2
            """, Tuple.of(idSottotipologiaBonifico, idCanale));
        }
    }

//    public class WithLinkedEntities implements EntityWithLinkedEntities {
//
//        SottoTipologiaBonifico.WithLinkedEntities sottoTipologiaBonifico;
//        Canale.WithLinkedEntities
//
//        @Override
//        public BonificoExtraSepaListaCanaliAbilitati getEntity() {
//            return BonificoExtraSepaListaCanaliAbilitati.this;
//        }
//
//        @Override
//        public UUID id() {
//            return getEntity().id();
//        }
//
//        @Override
//        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
//            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();
//
//            return Multi.createBy().merging().streams(multis);
//        }
//
//        @Override
//        /// Non creare canali
//        public Uni<Void> insert(SqlClient sqlClient) {
//            return Uni.createFrom().voidItem();
//        }
//
//        @Override
//        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
//
//        }
//    }
}