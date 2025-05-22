package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.address;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.persistence.*;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.DateKind;
import com.flowpay.ccp.persistence.Table;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

/**
 * Rappresenta un indirizzo postale, in conformità con gli standard ISO 20022.
 * <p>
 * Questa classe viene utilizzata per memorizzare i dettagli di un indirizzo,
 * che può essere associato a un'entità come un conto, un'istituzione finanziaria
 * o un'altra parte coinvolta in una transazione bancaria.
 * <p>
 * La tabella associata nel database è {@code indirizzo_postale}.
 * <p>
 * I campi di questa classe sono mappati su {@code PostalAddress24} di ISO 20022.
 * <p>
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE indirizzo_postale (
 *     id UUID PRIMARY KEY,
 *     indirizzo VARCHAR(255),
 *     citta VARCHAR(255),
 *     cap VARCHAR(10),
 *     paese VARCHAR(2),
 *     divisione VARCHAR(255),
 *     sotto_divisione VARCHAR(255),
 *     numero_civico VARCHAR(10),
 *     edificio VARCHAR(255),
 *     piano VARCHAR(10),
 *     cassetta_postale VARCHAR(50),
 *     stanza VARCHAR(50),
 *     localita VARCHAR(255),
 *     distretto VARCHAR(255),
 *     provincia VARCHAR(255),
 *     linea_indirizzo VARCHAR(255),
 *     created_at TIMESTAMP WITH TIME ZONE NOT NULL
 * );
 * }</pre>
 *
 * @param indirizzo         Nome della strada. <p>Mappato in {@code StrtNm} di PostalAddress24.</p>
 * @param citta             Nome della città. <p>Mappato in {@code TwnNm} di PostalAddress24.</p>
 * @param cap               Codice di avviamento postale (CAP). <p>Mappato in {@code PstCd} di PostalAddress24.</p>
 * @param paese             Paese, in formato ISO 3166-1 alpha-2. <p>Mappato in {@code Ctry} di PostalAddress24.</p>
 * @param divisione         Dipartimento o Stato. <p>Mappato in {@code Dept} di PostalAddress24.</p>
 * @param sottoDivisione    Sottodivisione amministrativa secondaria (es. provincia o distretto). <p>Mappato in {@code CtrySubDvsn} di PostalAddress24.</p>
 * @param numeroCivico      Numero civico dell'edificio. <p>Mappato in {@code BldgNb} di PostalAddress24.</p>
 * @param edificio          Nome dell'edificio, se applicabile. <p>Mappato in {@code BldgNm} di PostalAddress24.</p>
 * @param piano             Piano dell'edificio. <p>Mappato in {@code Flr} di PostalAddress24.</p>
 * @param cassettaPostale   Numero della cassetta postale. <p>Mappato in {@code PstBx} di PostalAddress24.</p>
 * @param stanza            Numero della stanza. <p>Mappato in {@code Room} di PostalAddress24.</p>
 * @param localita          Nome della località. <p>Mappato in {@code TwnLcltnNm} di PostalAddress24.</p>
 * @param distretto         Nome del distretto. <p>Mappato in {@code DstrctNm} di PostalAddress24.</p>
 * @param provincia         Nome della provincia. <p>Mappato in {@code CtrySubDvsn} di PostalAddress24.</p>
 * @param lineaIndirizzo    Linea di indirizzo aggiuntiva. <p>Mappato in {@code AdrLine} di PostalAddress24.</p>
 *
 * @see java.time.Instant
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("indirizzo_postale")
public record IndirizzoPostale(

        /// Identificativo univoco dell'indirizzo postale.
        UUID id,

        /// Nome della strada.
        /// <p>Mappato in `StrtNm` di PostalAddress24.</p>
        String indirizzo,

        /// Nome della città.
        /// <p>Mappato in `TwnNm` di PostalAddress24.</p>
        String citta,

        /// Codice di avviamento postale (CAP).
        /// <p>Mappato in `PstCd` di PostalAddress24.</p>
        String cap,

        /// Paese, in formato ISO 3166-1 alpha-2.
        /// <p>Mappato in `Ctry` di PostalAddress24.</p>
        String paese,

        /// Dipartimento o Stato.
        /// <p>Mappato in `Dept` di PostalAddress24.</p>
        String divisione,

        /// Sottodivisione amministrativa secondaria (es. provincia o distretto).
        /// <p>Mappato in `CtrySubDvsn` di PostalAddress24.</p>
        @Column("sotto_divisione")
        String sottoDivisione,

        /// Numero civico dell'edificio.
        /// <p>Mappato in `BldgNb` di PostalAddress24.</p>
        @Column("numero_civico")
        String numeroCivico,

        /// Nome dell'edificio, se applicabile.
        /// <p>Mappato in `BldgNm` di PostalAddress24.</p>
        String edificio,

        /// Piano dell'edificio.
        /// <p>Mappato in `Flr` di PostalAddress24.</p>
        String piano,

        /// Numero della cassetta postale.
        /// <p>Mappato in `PstBx` di PostalAddress24.</p>
        @Column("cassetta_postale")
        String cassettaPostale,

        /// Numero della stanza.
        /// <p>Mappato in `Room` di PostalAddress24.</p>
        String stanza,

        /// Nome della località.
        /// <p>Mappato in `TwnLcltnNm` di PostalAddress24.</p>
        String localita,

        /// Nome del distretto.
        /// <p>Mappato in `DstrctNm` di PostalAddress24.</p>
        String distretto,

        /// Nome della provincia.
        /// <p>Mappato in `CtrySubDvsn` di PostalAddress24.</p>
        String provincia,

        /// Linea di indirizzo aggiuntiva.
        /// <p>Mappato in `AdrLine` di PostalAddress24.</p>
        @Column("linea_indirizzo")
        String lineaIndirizzo,

        /// Timestamp di creazione dell'indirizzo
        @CreationTimeStamp 
        @Column("created_at") 
        @DateKind Instant createdAt
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<IndirizzoPostale, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<IndirizzoPostale> entityClass() {
            return IndirizzoPostale.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<IndirizzoPostale> {

        public Repository(SqlClient client, java.util.function.Function<io.vertx.mutiny.sqlclient.Row, IndirizzoPostale> decoder) {
            super(client, decoder);
        }

        public Uni<IndirizzoPostale> getById(UUID id) {
            return singleOrNull("SELECT * FROM indirizzo_postale WHERE id = $1", Tuple.of(id));
        }

    }
    
    /**
     * Prepara a recuperare le entity linkate.
     */
    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {

        @Override
        public IndirizzoPostale getEntity() {
            return IndirizzoPostale.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            // No linked entites to collect
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            return Multi.createBy().merging().streams(multis);
        }

        @Override
        public Uni<Void> insert(SqlClient sqlClient) {
            var entity = new Entity();
            return entity.repository(sqlClient).run(entity.insert(getEntity()));
        }
    }
}