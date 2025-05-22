package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.bank_to_bank;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.DettaglioBonificoCommon;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.RegolamentoCommissione;
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
 * Rappresenta il dettaglio di un bonifico banca a banca nel sistema di pagamento.<p>
 *
 * Questa classe mappa la tabella {@code dettaglio_bonifico_banca_a_banca} nel database e contiene
 * informazioni relative all'importo, alla divisa, al tasso di cambio e ai dettagli della transazione.<p>
 *
 * La classe è conforme agli standard ISO 20022 e include dettagli sulla causale della transazione
 * e altre informazioni rilevanti.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE dettaglio_bonifico_banca_a_banca (
 *     id UUID PRIMARY KEY,
 *     id_bonifico_extra_sepa UUID NOT NULL,
 *     importo NUMERIC(18,2) NOT NULL,
 *     divisa VARCHAR(3) NOT NULL,
 *     cambio NUMERIC(18,6),
 *     importo_di_addebito NUMERIC(18,2),
 *     codice_causale_transazione VARCHAR(255),
 *     informazioni_aggiuntive_notifica VARCHAR(255),
 *     CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
 *         REFERENCES bonifico_extra_sepa(id)
 * );
 * }</pre>
 *
 * @param id                          Identificativo univoco del dettaglio bonifico<p>
 * @param idBonificoExtraSepa         Identificativo del bonifico extra-SEPA associato<p>
 * @param importo                     Importo del bonifico ({@code CdtTrfTxInf/IntrBkSttlmAmt})<p>
 * @param divisa                      Divisa del bonifico ({@code CdtTrfTxInf/IntrBkSttlmAmt/Ccy})<p>
 * @param cambio                      Tasso di cambio applicato<p>
 * @param causaleDescrittiva          Causale descrittiva della transazione ({@code CdtTrfTxInf/RmtInf/Ustrd})<p>
 * @param codiceCausaleTransazione    Codice della causale della transazione<p>
 * @param informazioniAggiuntiveNotifica Informazioni aggiuntive per la notifica (ISO 20022: {@code AddtlNtfctnInf})<p>
 */
@Table("dettaglio_bonifico_banca_a_banca")
public record DettaglioBonificoBancaABanca(

        /// Identificativo univoco del dettaglio bonifico
        UUID id,

        /// Identificativo del bonifico extra-SEPA associato
        @Column("id_bonifico_extra_sepa")
        UUID idBonificoExtraSepa,

        /// Importo del bonifico (ISO 20022: {@code CdtTrfTxInf/IntrBkSttlmAmt})
        BigDecimal importo,

        /// Divisa del bonifico (ISO 20022: {@code CdtTrfTxInf/IntrBkSttlmAmt/Ccy})
        String divisa,

        /// Tasso di cambio applicato alla transazione
        @Column("cambio")
        BigDecimal cambio,

        @Column("regolamento_commissioni_banca")
        RegolamentoCommissione regolamentoCommissioneBanca,

        /// Importo effettivamente addebitato
        @Column("importo_di_addebito")
        BigDecimal importoDiAddebito,

        /// Codice della causale della transazione
        @Column("codice_causale_transazione")
        String codiceCausaleTransazione,

        /// Informazioni aggiuntive per la notifica (ISO 20022: {@code AddtlNtfctnInf})
        @Column("informazioni_aggiuntive_notifica")
        String informazioniAggiuntiveNotifica
     
) implements DettaglioBonificoCommon {
    public static final class Entity
            implements com.flowpay.ccp.persistence.Entity<DettaglioBonificoBancaABanca, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<DettaglioBonificoBancaABanca> entityClass() {
            return DettaglioBonificoBancaABanca.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<DettaglioBonificoBancaABanca> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, DettaglioBonificoBancaABanca> decoder) {
            super(client, decoder);
        }

        public Uni<DettaglioBonificoBancaABanca> getByBonificoExtraSepa(UUID idBonificoExtraSepa) {
            return singleOrNull("SELECT * FROM dettaglio_bonifico_banca_a_banca WHERE id_bonifico_extra_sepa = $1",
                    Tuple.of(idBonificoExtraSepa));
        }

    }

    /**
     * Prepara a recuperare le entity linkate.
     */
    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {
        public BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa;

        @Override
        public DettaglioBonificoBancaABanca getEntity() {
            return DettaglioBonificoBancaABanca.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(bonificoExtraSepa);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.bonificoExtraSepa == null) {
                multis.add(new BonificoExtraSepa.Entity()
                        .repository(sqlClient).getById(idBonificoExtraSepa())
                        .map(linked -> {
                            this.bonificoExtraSepa = linked.withLinkedEntities();
                            this.bonificoExtraSepa.dettaglioBonificoBancaABanca = this;
                            return this.bonificoExtraSepa;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.bonificoExtraSepa));
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