package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
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
 * Rappresenta le informazioni di un conto bancario secondo lo schema ISO 20022 {@code CashAccount38}.
 * <p>
 * Questa classe memorizza i dettagli di un conto, come IBAN, identificativi alternativi,
 * divisa, tipo di conto e informazioni sull'alias, utilizzati nei bonifici internazionali.
 * <p>
 * La tabella associata nel database è {@code informazioni_rapporto}.
 * <p>
 * I campi di questa classe sono mappati su {@code CashAccount38} di ISO 20022.
 * <p>
 * SQL per la creazione della tabella:
 * <pre>{@code
 * CREATE TABLE informazioni_rapporto (
 *     id UUID PRIMARY KEY,
 *     iban VARCHAR(34),
 *     altro_id VARCHAR(255),
 *     divisa VARCHAR(3),
 *     codice_tipo_conto VARCHAR(255),
 *     dettaglio_tipo_conto VARCHAR(255),
 *     intestazione_conto VARCHAR(255),
 *     codice_tipo_alias VARCHAR(255),
 *     descrizione_alias VARCHAR(255),
 *     dettaglio_identificativo_alias VARCHAR(255),
 *     codice_identificativo_conto VARCHAR(255),
 *     descrizione_identificativo_conto VARCHAR(255),
 *     emittente VARCHAR(255),
 *     codice_filiale VARCHAR(255),
 *     denominazione_filiale VARCHAR(255)
 * );
 * }</pre>
 *
 * @param id                        Identificativo univoco del record.
 * @param tipoRapporto              Tipologia di rapporto (rapporto normale o sotto conto)
 * @param ndg                       Identificativo dell'utente collegato a questo conto
 * @param iban                      IBAN del conto. <p>Mappato in {@code Id/Iban} di CashAccount38.</p>
 * @param altroID                   Identificativo alternativo del conto. <p>Mappato in {@code Id/Othr/Id} di CashAccount38.</p>
 * @param divisa                    Valuta del conto. <p>Mappato in {@code Ccy} di CashAccount38.</p>
 * @param codiceTipoConto           Codice tipo conto. <p>Mappato in {@code Tp/Cd} di CashAccount38.</p>
 * @param dettaglioTipoConto        Dettaglio tipo conto. <p>Mappato in {@code Tp/Prtry} di CashAccount38.</p>
 * @param intestazioneConto                 Nome del conto. <p>Mappato in {@code Nm} di CashAccount38.</p>
 * @param codiceTipoAlias           Tipo alias codice. <p>Mappato in {@code Prxy/Tp/Cd} di CashAccount38.</p>
 * @param descrizioneAlias          Descrizione alias. <p>Mappato in {@code Prxy/Tp/Prtry} di CashAccount38.</p>
 * @param dettaglioIdentificativoAlias Identificativo alias. <p>Mappato in {@code Prxy/Id} di CashAccount38.</p>
 * @param codiceIdentificativoConto Codice identificativo del conto. <p>Mappato in {@code Id/Othr/SchemeNm/Cd} di CashAccount38.</p>
 * @param descrizioneIdentificativoConto Descrizione identificativo del conto. <p>Mappato in {@code Id/Othr/SchemeNm/Prtry} di CashAccount38.</p>
 * @param emittente                 Emittente dell'identificativo del conto. <p>Mappato in {@code Id/Othr/Issr} di CashAccount38.</p>
 * @param codiceFiliale             Codice interno della filiale del conto
 * @param denominazioneFiliale      Nome della filiale del conto
 *
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("informazioni_rapporto")
public record InformazioniRapporto(

        /// Identificativo univoco del record.
        UUID id,

        String numero,

        @Column("tipo_rapporto")
        TipoRapporto tipoRapporto,

        String ndg,

        /// IBAN del conto.
        /// <p>Mappato in `Id/Iban` di CashAccount38.</p>
        String iban,

        /// Identificativo alternativo del conto.
        /// <p>Mappato in `Id/Othr/Id` di CashAccount38.</p>
        @Column("altro_id")
        String altroID,

        /// Valuta del conto.
        /// <p>Mappato in `Ccy` di CashAccount38.</p>
        String divisa,

        /// Codice tipo conto.
        /// <p>Mappato in `Tp/Cd` di CashAccount38.</p>
        @Column("codice_tipo_conto")
        String codiceTipoConto,

        /// Dettaglio tipo conto.
        /// <p>Mappato in `Tp/Prtry` di CashAccount38.</p>
        @Column("dettaglio_tipo_conto")
        String dettaglioTipoConto,

        /// Nome del conto.
        /// <p>Mappato in `Nm` di CashAccount38.</p>
        @Column("intestazione_conto")
        String intestazioneConto,

        /// Tipo alias codice.
        /// <p>Mappato in `Prxy/Tp/Cd` di CashAccount38.</p>
        @Column("codice_tipo_alias")
        String codiceTipoAlias,

        /// Descrizione alias.
        /// <p>Mappato in `Prxy/Tp/Prtry` di CashAccount38.</p>
        @Column("descrizione_alias")
        String descrizioneAlias,

        /// Identificativo alias.
        /// <p>Mappato in `Prxy/Id` di CashAccount38.</p>
        @Column("dettaglio_identificativo_alias")
        String dettaglioIdentificativoAlias,

        /// Codice identificativo del conto.
        /// <p>Mappato in `Id/Othr/SchemeNm/Cd` di CashAccount38.</p>
        @Column("codice_identificativo_conto")
        String codiceIdentificativoConto,

        /// Descrizione identificativo del conto.
        /// <p>Mappato in `Id/Othr/SchemeNm/Prtry` di CashAccount38.</p>
        @Column("descrizione_identificativo_conto")
        String descrizioneIdentificativoConto,

        /// Emittente dell'identificativo del conto.
        /// <p>Mappato in `Id/Othr/Issr` di CashAccount38.</p>
        String emittente,


        @Column("codice_filiale")
        Long codiceFiliale,
        @Column("denominazione_filiale")
        String denominazioneFiliale
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<InformazioniRapporto, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<InformazioniRapporto> entityClass() {
            return InformazioniRapporto.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<InformazioniRapporto> {

        public Repository(SqlClient client, java.util.function.Function<io.vertx.mutiny.sqlclient.Row, InformazioniRapporto> decoder) {
            super(client, decoder);
        }

        public Uni<InformazioniRapporto> getById(UUID id) {
            return singleOrNull("SELECT * FROM informazioni_rapporto WHERE id = $1", Tuple.of(id));
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
        public InformazioniRapporto getEntity() {
            return InformazioniRapporto.this;
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