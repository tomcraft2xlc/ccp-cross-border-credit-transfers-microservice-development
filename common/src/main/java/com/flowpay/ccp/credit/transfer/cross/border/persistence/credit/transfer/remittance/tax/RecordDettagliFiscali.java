package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.DateKind;
import com.flowpay.ccp.persistence.Table;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Rappresenta i dettagli di un record fiscale associato a una transazione di pagamento,<p>
 * secondo lo schema ISO 20022 {@code RmtInf/Strd/TaxRmt/Rcrd}.<p>
 *
 * Questa classe memorizza le informazioni fiscali rilevanti, incluse le categorie,
 * il periodo di riferimento, la percentuale di imposta e gli importi.<p>
 *
 * La tabella associata nel database è {@code record_dettagli_fiscali}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE record_dettagli_fiscali (
 *     id UUID PRIMARY KEY,
 *     id_dettagli_fiscali UUID NOT NULL,
 *     codice_tipo VARCHAR(255),
 *     categoria_tassazione VARCHAR(255),
 *     dettagli_categoria_tassazione VARCHAR(255),
 *     status_contribuente_debitore VARCHAR(255),
 *     identificativo_dichiarazione VARCHAR(255),
 *     codice_modello_dichiarazione VARCHAR(255),
 *     anno_riferimento_dichiarazione VARCHAR(255),
 *     periodo_riferimento_dichiarazione VARCHAR(255),
 *     periodo_riferimento_dichiarazione_da DATE,
 *     periodo_riferimento_dichiarazione_a DATE,
 *     percentuale_imposta NUMERIC(5,2),
 *     importo_imponibile NUMERIC(18,2),
 *     divisa_importo_imponibile VARCHAR(3),
 *     importo_imposta NUMERIC(18,2),
 *     divisa_importo_imposta VARCHAR(3),
 *     informazioni_aggiuntive TEXT,
 *     CONSTRAINT fk_dettagli_fiscali FOREIGN KEY (id_dettagli_fiscali)
 *         REFERENCES dettagli_fiscali(id)
 * );
 * }</pre>
 *
 * @param id                                  Identificativo univoco del record fiscale.<p>
 * @param idDettagliFiscali                    Identificativo dei dettagli fiscali associati.<p>
 * @param codiceTipo                           Codice del tipo di tassazione.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/Tp}.</p>
 * @param categoriaTassazione                  Categoria di tassazione.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/Ctgy}.</p>
 * @param dettagliCategoriaTassazione          Dettagli sulla categoria di tassazione.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/CtgyDtls}.</p>
 * @param statusContribuenteDebitore           Stato del contribuente debitore.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/DbtrSts}.</p>
 * @param identificativoDichiarazione          Identificativo della dichiarazione fiscale.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/CertId}.</p>
 * @param codiceModelloDichiarazione           Codice del modello di dichiarazione.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/FrmsCd}.</p>
 * @param annoRiferimentoDichiarazione         Anno di riferimento della dichiarazione.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/Prd/Yr}.</p>
 * @param periodoRiferimentoDichiarazione      Periodo di riferimento per la dichiarazione.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/Prd/Tp}.</p>
 * @param periodoRiferimentoDichiarazioneDa    Data di inizio del periodo di riferimento.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/Prd/FrToDt/FrDt}.</p>
 * @param periodoRiferimentoDichiarazioneA     Data di fine del periodo di riferimento.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/Prd/FrToDt/ToDt}.</p>
 * @param percentualeImposta                   Percentuale di imposta applicata.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/Rate}.</p>
 * @param importoImponibile                    Importo imponibile soggetto a tassazione.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/TaxblBaseAmt}.</p>
 * @param divisaImportoImponibile              Divisa dell'importo imponibile.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/TaxblBaseAmt/Ccy}.</p>
 * @param importoImposta                       Importo totale dell'imposta.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/TtlAmt}.</p>
 * @param divisaImportoImposta                 Divisa dell'importo dell'imposta.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/TtlAmt/Ccy}.</p>
 * @param informazioniAggiuntive               Informazioni aggiuntive relative alla tassazione.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/AddtlInf}.</p>
 *
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("record_dettagli_fiscali")
public record RecordDettagliFiscali(
        UUID id,

        @Column("id_dettagli_fiscali")
        UUID idDettagliFiscali,

        @Column("codice_tipo")
        String codiceTipo,

        @Column("categoria_tassazione")
        String categoriaTassazione,

        @Column("dettagli_categoria_tassazione")
        String dettagliCategoriaTassazione,

        @Column("status_contribuente_debitore")
        String statusContribuenteDebitore,

        @Column("identificativo_dichiarazione")
        String identificativoDichiarazione,

        @Column("codice_modello_dichiarazione")
        String codiceModelloDichiarazione,

        @Column("anno_riferimento_dichiarazione")
        String annoRiferimentoDichiarazione,

        @Column("periodo_riferimento_dichiarazione")
        String periodoRiferimentoDichiarazione,

        @Column("periodo_riferimento_dichiarazione_da")
        @DateKind(DateKind.DateKindEnum.DATE)
        LocalDate periodoRiferimentoDichiarazioneDa,

        @Column("periodo_riferimento_dichiarazione_a")
        @DateKind(DateKind.DateKindEnum.DATE)
        LocalDate periodoRiferimentoDichiarazioneA,

        @Column("percentuale_imposta")
        BigDecimal percentualeImposta,

        @Column("importo_imponibile")
        BigDecimal importoImponibile,

        @Column("divisa_importo_imponibile")
        String divisaImportoImponibile,

        @Column("importo_imposta")
        BigDecimal importoImposta,

        @Column("divisa_importo_imposta")
        String divisaImportoImposta,

        @Column("informazioni_aggiuntive")
        String informazioniAggiuntive
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<RecordDettagliFiscali, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<RecordDettagliFiscali> entityClass() {
            return RecordDettagliFiscali.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<RecordDettagliFiscali> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, RecordDettagliFiscali> decoder) {
            super(client, decoder);
        }

        public Uni<RecordDettagliFiscali> getById(UUID id) {
            return single("SELECT * FROM record_dettagli_fiscali WHERE id = $1",
                    Tuple.of(id));
        }

        public Multi<RecordDettagliFiscali> getAllByDettagliFiscali(UUID idDettagliFiscali) {
            return multi("SELECT * FROM record_dettagli_fiscali WHERE id_dettagli_fiscali = $1",
                    Tuple.of(idDettagliFiscali));
        }
    }

    /**
     * Prepara a recuperare le entity linkate.
     */
    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {
        public DettagliFiscali.WithLinkedEntities dettagliFiscali;
        public Collection<DettagliRecordDettagliFiscali.WithLinkedEntities> dettagliRecordDettagliFiscali;

        @Override
        public RecordDettagliFiscali getEntity() {
            return RecordDettagliFiscali.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(dettagliFiscali);
            if (dettagliRecordDettagliFiscali != null) dettagliRecordDettagliFiscali.forEach(consumer);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.dettagliFiscali == null) {
                multis.add(new DettagliFiscali.Entity()
                        .repository(sqlClient).getById(idDettagliFiscali())
                        .map(linked -> {
                            this.dettagliFiscali = linked.withLinkedEntities();
                            // Adding this to the parent would not be so beneficial as
                            // the sibling would need to be loaded, and loading is
                            // done en-masse.
                            return this.dettagliFiscali;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.dettagliFiscali));
            }

            if (this.dettagliRecordDettagliFiscali == null) {
                this.dettagliRecordDettagliFiscali = new ArrayList<>();
                multis.add(new DettagliRecordDettagliFiscali.Entity()
                        .repository(sqlClient).getAllByRecordDettagliFiscali(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.recordDettagliFiscali = this;
                            this.dettagliRecordDettagliFiscali.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.dettagliRecordDettagliFiscali).onItem()
                        .castTo(EntityWithLinkedEntities.class));
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