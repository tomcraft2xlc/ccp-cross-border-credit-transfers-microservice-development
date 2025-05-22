package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.TipoDettaglioImportoDocumentoDiRiferimento;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.Table;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Rappresenta i dettagli relativi all'importo di un documento di riferimento,<p>
 * secondo lo schema ISO 20022 {@code RmtInf/Strd/RfrdDocAmt}.<p>
 *
 * Questa classe memorizza informazioni dettagliate sui tipi di importo,
 * inclusi il tipo, il motivo, il verso e eventuali informazioni aggiuntive.<p>
 *
 * La tabella associata nel database è {@code dettaglio_importo_documento_di_riferimento}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE dettaglio_importo_documento_di_riferimento (
 *     id UUID PRIMARY KEY,
 *     tipo_dettaglio_importo_documento_di_riferimento tipo_dettaglio_importo_documento_di_riferimento NOT NULL,
 *     tipo VARCHAR(255),
 *     dettagli VARCHAR(255),
 *     importo NUMERIC(18,2),
 *     divisa VARCHAR(3),
 *     verso VARCHAR(255),
 *     motivo VARCHAR(255),
 *     informazioni_aggiuntive TEXT
 * );
 * }</pre>
 *
 * @param id                                        Identificativo univoco del record.<p>
 * @param tipoDettaglioImportoDocumentoDiRiferimento Tipo del dettaglio dell'importo del documento di riferimento.<p>Mappato in {@code RmtInf/Strd/RfrdDocAmt/<tipo>}.</p>
 * @param tipo                                      Tipo di importo.<p>Mappato in {@code RmtInf/Strd/RfrdDocAmt/<tipo>/Tp/Cd}.</p>
 * @param dettagli                                  Dettagli aggiuntivi relativi al tipo.<p>Mappato in {@code RmtInf/Strd/RfrdDocAmt/<tipo>/Tp/Prtry}.</p>
 * @param importo                                   Importo specifico associato al documento.<p>Mappato in {@code RmtInf/Strd/RfrdDocAmt/<tipo>/Amt}.</p>
 * @param divisa                                    Divisa dell'importo.<p>Mappato in {@code RmtInf/Strd/RfrdDocAmt/<tipo>/Amt/Ccy}.</p>
 * @param verso                                     Direzione dell'importo (ad esempio credito o debito).<p>Mappato in {@code RmtInf/Strd/RfrdDocAmt/<tipo>/CdtDbtInd}.</p>
 * @param motivo                                    Motivo dell'importo.<p>Mappato in {@code RmtInf/Strd/RfrdDocAmt/<tipo>/Rsn}.</p>
 * @param informazioniAggiuntive                    Ulteriori informazioni relative all'importo.<p>Mappato in {@code RmtInf/Strd/RfrdDocAmt/<tipo>/AddtlInf}.</p>
 *
 * @see TipoDettaglioImportoDocumentoDiRiferimento
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("dettaglio_importo")
public record DettaglioImporto(

        /// Identificativo univoco del record.
        UUID id,

        /// Tipo del dettaglio dell'importo del documento di riferimento.
        /// <p>Mappato in `RmtInf/Strd/RfrdDocAmt/<tipo>`.</p>
        @Column("tipo_dettaglio_importo_documento_di_riferimento")
        TipoDettaglioImportoDocumentoDiRiferimento tipoDettaglioImportoDocumentoDiRiferimento,

        /// Tipo di importo.
        /// <p>Mappato in `RmtInf/Strd/RfrdDocAmt/<tipo>/Tp/Cd`.</p>
        String tipo,

        /// Dettagli aggiuntivi relativi al tipo.
        /// <p>Mappato in `RmtInf/Strd/RfrdDocAmt/<tipo>/Tp/Prtry`.</p>
        String dettagli,

        /// Importo specifico associato al documento.
        /// <p>Mappato in `RmtInf/Strd/RfrdDocAmt/<tipo>/Amt`.</p>
        BigDecimal importo,

        /// Divisa dell'importo.
        /// <p>Mappato in `RmtInf/Strd/RfrdDocAmt/<tipo>/Amt/Ccy`.</p>
        String divisa,

        /// Direzione dell'importo (credito o debito).
        /// <p>Mappato in `RmtInf/Strd/RfrdDocAmt/<tipo>/CdtDbtInd`.</p>
        VersoEnum verso,

        /// Motivo dell'importo.
        /// <p>Mappato in `RmtInf/Strd/RfrdDocAmt/<tipo>/Rsn`.</p>
        String motivo,

        /// Ulteriori informazioni relative all'importo.
        /// <p>Mappato in `RmtInf/Strd/RfrdDocAmt/<tipo>/AddtlInf`.</p>
        @Column("informazioni_aggiuntive")
        String informazioniAggiuntive
) {

    public static final class Entity
            implements com.flowpay.ccp.persistence.Entity<DettaglioImporto, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<DettaglioImporto> entityClass() {
            return DettaglioImporto.class;
        }
    }

    public static final class Repository
            extends com.flowpay.ccp.persistence.Repository<DettaglioImporto> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, DettaglioImporto> decoder) {
            super(client, decoder);
        }

        public Uni<DettaglioImporto> getById(UUID id) {
            return single("SELECT * FROM dettaglio_importo WHERE id = $1", Tuple.of(id));
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
        public DettaglioImporto getEntity() {
            return DettaglioImporto.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            // No linked entities
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