package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.Table;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Rappresenta i dettagli di un record dei dettagli fiscali relativi a una transazione di pagamento,<p>
 * secondo lo schema ISO 20022 {@code RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/Dtls}.<p>
 *
 * Questa classe memorizza informazioni sul periodo di riferimento,
 * gli importi fiscali e la divisa utilizzata.<p>
 *
 * La tabella associata nel database è {@code dettagli_record_dettagli_fiscali}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE dettagli_record_dettagli_fiscali (
 *     id UUID PRIMARY KEY,
 *     id_record_dettagli_fiscali UUID NOT NULL,
 *     anno_riferimento VARCHAR(255),
 *     periodo_riferimento VARCHAR(255),
 *     periodo_riferimento_da DATE,
 *     periodo_riferimento_a DATE,
 *     importo NUMERIC(18,2),
 *     divisa VARCHAR(3),
 *     CONSTRAINT fk_record_dettagli_fiscali FOREIGN KEY (id_record_dettagli_fiscali)
 *         REFERENCES record_dettagli_fiscali(id)
 * );
 * }</pre>
 *
 * @param id                          Identificativo univoco del record.<p>
 * @param idRecordDettagliFiscali      Identificativo del record dei dettagli fiscali associato.<p>
 * @param annoRiferimento              Anno di riferimento per il calcolo delle tasse.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/Dtls/Prd/Yr}.</p>
 * @param periodoRiferimento           Tipo di periodo di riferimento per la tassazione.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/Dtls/Prd/Tp}.</p>
 * @param periodoRiferimentoDa         Data di inizio del periodo di riferimento fiscale.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/Dtls/Prd/FrToDt/FrDt}.</p>
 * @param periodoRiferimentoA          Data di fine del periodo di riferimento fiscale.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/Dtls/Prd/FrToDt/ToDt}.</p>
 * @param importo                      Importo fiscale riferito al periodo.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/Dtls/Amt}.</p>
 * @param divisa                       Divisa dell'importo fiscale.<p>Mappato in {@code RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/Dtls/Amt/Ccy}.</p>
 *
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("dettagli_record_dettagli_fiscali")
public record DettagliRecordDettagliFiscali(
        UUID id,

        @Column("id_record_dettagli_fiscali")
        UUID idRecordDettagliFiscali,

        /// Anno di riferimento per il calcolo delle tasse.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/Dtls/Prd/Yr`.</p>
        @Column("anno_riferimento")
        String annoRiferimento,

        /// Tipo di periodo di riferimento per la tassazione.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/Dtls/Prd/Tp`.</p>
        @Column("periodo_riferimento")
        String periodoRiferimento,

        /// Data di inizio del periodo di riferimento fiscale.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/Dtls/Prd/FrToDt/FrDt`.</p>
        @Column("periodo_riferimento_da")
        LocalDate periodoRiferimentoDa,

        /// Data di fine del periodo di riferimento fiscale.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/Dtls/Prd/FrToDt/ToDt`.</p>
        @Column("periodo_riferimento_a")
        LocalDate periodoRiferimentoA,

        /// Importo fiscale riferito al periodo.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/Dtls/Amt`.</p>
        BigDecimal importo,

        /// Divisa dell'importo fiscale.
        /// <p>Mappato in `RmtInf/Strd/TaxRmt/Rcrd/TaxAmt/Dtls/Amt/Ccy`.</p>
        String divisa
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<DettagliRecordDettagliFiscali, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<DettagliRecordDettagliFiscali> entityClass() {
            return DettagliRecordDettagliFiscali.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<DettagliRecordDettagliFiscali> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, DettagliRecordDettagliFiscali> decoder) {
            super(client, decoder);
        }

        public Multi<DettagliRecordDettagliFiscali> getAllByRecordDettagliFiscali(UUID idRecordDettagliFiscali) {
            return multi("SELECT * FROM dettagli_record_dettagli_fiscali WHERE id_record_dettagli_fiscali = $1",
                    Tuple.of(idRecordDettagliFiscali));
        }
    
    }

    
    /**
     * Prepara a recuperare le entity linkate.
     */
    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {
        public RecordDettagliFiscali.WithLinkedEntities recordDettagliFiscali;

        @Override
        public DettagliRecordDettagliFiscali getEntity() {
            return DettagliRecordDettagliFiscali.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(recordDettagliFiscali);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.recordDettagliFiscali == null) {
                multis.add(new RecordDettagliFiscali.Entity()
                        .repository(sqlClient).getById(idRecordDettagliFiscali())
                        .map(linked -> {
                            this.recordDettagliFiscali = linked.withLinkedEntities();
                            // Adding this to the parent would not be so beneficial as
                            // the sibling would need to be loaded, and loading is
                            // done en-masse.
                            return this.recordDettagliFiscali;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.recordDettagliFiscali));
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