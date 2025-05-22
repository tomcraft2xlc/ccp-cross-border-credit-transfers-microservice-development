package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.InformazioniCausale;
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
 * Rappresenta i dettagli fiscali relativi a una transazione di pagamento,<p>
 * secondo lo schema ISO 20022 {@code RmtInf/Strd/TaxRmt}.<p>
 *
 * Questa classe memorizza informazioni sulle tasse applicabili,
 * tra cui l'amministrazione di riferimento, l'importo dell'imposta,
 * la divisa, il metodo di pagamento e la scadenza fiscale.<p>
 *
 * La tabella associata nel database è {@code dettagli_fiscali}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE dettagli_fiscali (
 *     id UUID PRIMARY KEY,
 *     id_informazioni_causale UUID NOT NULL,
 *     amministrazione_di_riferimento VARCHAR(255),
 *     dettaglio_imposta_riferimento VARCHAR(255),
 *     metodo VARCHAR(255),
 *     importo_imponibile NUMERIC(18,2),
 *     divisa_importo_imponibile VARCHAR(3),
 *     importo_imposta NUMERIC(18,2),
 *     divisa_importo_imposta VARCHAR(3),
 *     scadenza DATE,
 *     numero_progressivo_dichiarazione NUMERIC(18,2),
 *     CONSTRAINT fk_informazioni_causale FOREIGN KEY (id_informazioni_causale)
 *         REFERENCES informazioni_causale(id)
 * );
 * }</pre>
 *
 * @param id                                Identificativo univoco del record.<p>
 * @param idInformazioniCausale             Identificativo dell'informazione causale associata.<p>
 * @param amministrazioneDiRiferimento      Amministrazione di riferimento.<p>Mappato in {@code RmtInf/Strd/TaxRmt/AdmstnZone}.</p>
 * @param dettaglioImpostaRiferimento       Dettaglio di riferimento dell'imposta.<p>Mappato in {@code RmtInf/Strd/TaxRmt/RefNb}.</p>
 * @param metodo                            Metodo di pagamento fiscale.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Mtd}.</p>
 * @param importoImponibile                 Importo imponibile su cui viene calcolata l'imposta.<p>Mappato in {@code RmtInf/Strd/TaxRmt/TtlTaxblBaseAmt}.</p>
 * @param divisaImportoImponibile           Divisa dell'importo imponibile.<p>Mappato in {@code RmtInf/Strd/TaxRmt/TtlTaxblBaseAmt/Ccy}.</p>
 * @param importoImposta                    Importo totale dell'imposta.<p>Mappato in {@code RmtInf/Strd/TaxRmt/TtlTaxAmt}.</p>
 * @param divisaImportoImposta              Divisa dell'importo dell'imposta.<p>Mappato in {@code RmtInf/Strd/TaxRmt/TtlTaxAmt/Ccy}.</p>
 * @param scadenza                          Data di scadenza per il pagamento dell'imposta.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Dt}.</p>
 * @param numeroProgressivoDichiarazione    Numero progressivo della dichiarazione fiscale.<p>Mappato in {@code RmtInf/Strd/TaxRmt/SeqNb}.</p>
 *
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("dettagli_fiscali")
public record DettagliFiscali(
        UUID id,

        @Column("id_informazioni_causale")
        UUID idInformazioniCausale,

        /// Amministrazione di riferimento.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/AdmstnZone`.</p>
        @Column("amministrazione_di_riferimento")
        String amministratoreDiRiferimento,

        /// Dettaglio di riferimento dell'imposta.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/RefNb`.</p>
        @Column("dettaglio_imposta_riferimento")
        String dettaglioImpostaRiferimento,

        /// Metodo di pagamento fiscale.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/Mtd`.</p>
        String metodo,

        /// Importo imponibile su cui viene calcolata l'imposta.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/TtlTaxblBaseAmt`.</p>
        @Column("importo_imponibile")
        BigDecimal importoImponibile,

        /// Divisa dell'importo imponibile.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/TtlTaxblBaseAmt/Ccy`.</p>
        @Column("divisa_importo_imponibile")
        String divisaImportoImponibile,

        /// Importo totale dell'imposta.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/TtlTaxAmt`.</p>
        @Column("importo_imposta")
        BigDecimal importoImposta,

        /// Divisa dell'importo dell'imposta.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/TtlTaxAmt/Ccy`.</p>
        @Column("divisa_importo_imposta")
        String divisaImportoImposta,

        /// Data di scadenza per il pagamento dell'imposta.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/Dt`.</p>
        @DateKind(DateKind.DateKindEnum.DATE)
        LocalDate scadenza,

        /// Numero progressivo della dichiarazione fiscale.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/SeqNb`.</p>
        @Column("numero_progressivo_dichiarazione")
        BigDecimal numeroProgressivoDichiarazione
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<DettagliFiscali, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<DettagliFiscali> entityClass() {
            return DettagliFiscali.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<DettagliFiscali> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, DettagliFiscali> decoder) {
            super(client, decoder);
        }

        public Uni<DettagliFiscali> getByInformazioniCausale(UUID idInformazioniCausale) {
            return singleOrNull("SELECT * FROM dettagli_fiscali WHERE id_informazioni_causale = $1",
                    Tuple.of(idInformazioniCausale));
        }

        public Uni<DettagliFiscali> getById(UUID id) {
            return single("SELECT * FROM dettagli_fiscali WHERE id = $1",
                    Tuple.of(id));
        }
    }

    /**
     * Prepara a recuperare le entity linkate.
     */
    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {
        public InformazioniCausale.WithLinkedEntities informazioniCausale;
        public Collection<AttoreFiscale.WithLinkedEntities> attoriFiscali;
        public Collection<RecordDettagliFiscali.WithLinkedEntities> recordsDettagliFiscali;

        @Override
        public DettagliFiscali getEntity() {
            return DettagliFiscali.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(informazioniCausale);
            if (attoriFiscali != null) attoriFiscali.forEach(consumer);
            if (recordsDettagliFiscali != null) recordsDettagliFiscali.forEach(consumer);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.informazioniCausale == null) {
                multis.add(new InformazioniCausale.Entity()
                        .repository(sqlClient).getById(idInformazioniCausale())
                        .map(linked -> {
                            this.informazioniCausale = linked.withLinkedEntities();
                            this.informazioniCausale.dettagliFiscali = this;
                            return this.informazioniCausale;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.informazioniCausale));
            }
            if (this.attoriFiscali == null) {
                this.attoriFiscali = new ArrayList<>();
                multis.add(new AttoreFiscale.Entity()
                        .repository(sqlClient).getAllByDettagliFiscali(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.dettagliFiscali = this;
                            this.attoriFiscali.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.attoriFiscali).onItem()
                        .castTo(EntityWithLinkedEntities.class));
            }
            if (this.recordsDettagliFiscali == null) {
                this.recordsDettagliFiscali = new ArrayList<>();
                multis.add(new RecordDettagliFiscali.Entity()
                        .repository(sqlClient).getAllByDettagliFiscali(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.dettagliFiscali = this;
                            this.recordsDettagliFiscali.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.recordsDettagliFiscali).onItem()
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