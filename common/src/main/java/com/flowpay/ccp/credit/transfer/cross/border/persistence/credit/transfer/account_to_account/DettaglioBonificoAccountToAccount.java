package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account_to_account;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.DettaglioBonificoCommon;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.RegolamentoCommissione;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.TipologiaCommissioni;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.Table;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Rappresenta il dettaglio di un bonifico account-to-account nel sistema di pagamento.
 * <p>
 * Questa classe mappa la tabella {@code dettaglio_bonifico_account_to_account} nel database e contiene
 * informazioni relative all'importo, alla divisa, al valore del cambio, al titolare effettivo,
 * al presentatore e ad altre informazioni di transazione.
 * <p>
 * La classe è conforme agli standards ISO 20022 e include dettagli sulla causale della transazione
 * e su eventuali identificativi di pagamento correlati.
 * <p>
 * SQL per la creazione della tabella:
 * <pre>{@code
 * CREATE TABLE dettaglio_bonifico_account_to_account (
 *  id UUID PRIMARY KEY,
 *  id_bonifico_extra_sepa UUID NOT NULL,
 *  importo NUMERIC(18,2) NOT NULL,
 *  divisa VARCHAR(3) NOT NULL,
 *  valore_cambio NUMERIC(18,6),
 *  importo_di_addebito NUMERIC(18,2),
 *  codice_causale_transazione VARCHAR(255),
 *  altro_identificativo_pagamento VARCHAR(255),
 *
 *  -- Vincolo di chiave esterna
 *  CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
 *      REFERENCES bonifico_extra_sepa(id)
 * );
 * }</pre>
 *
 * @param id                          Identificativo univoco del dettaglio bonifico</p>
 * @param idBonificoExtraSepa         Identificativo del bonifico extra-SEPA associato</p>
 * @param importo                     Importo del bonifico ({@code IntrBkSttlmAmt})</p>
 * @param divisa                      Divisa del bonifico ({@code IntrBkSttlmAmt/Ccy})</p>
 * @param valoreCambio                Valore del cambio applicato</p>
 * @param importoDiAddebito           Importo effettivamente addebitato</p>
 * @param codiceCausaleTransazione    Codice della causale della transazione</p>
 * @param altroIdentificativoPagamento Identificativo alternativo del pagamento ({@code RltdRmtInf/RmtId})</p>
 */
@Table("dettaglio_bonifico_account_to_account")
public record DettaglioBonificoAccountToAccount(

        /// Identificativo univoco del dettaglio bonifico
        UUID id,

        /// Identificativo del bonifico extra-SEPA associato
        @Column("id_bonifico_extra_sepa")
        UUID idBonificoExtraSepa,

        @Column("tipologia_commissioni")
        TipologiaCommissioni tipologiaCommissioni,

        @Column("regolamento_commissioni_clientela")
        RegolamentoCommissione regolamentoCommissioneClientela,

        @Column("regolamento_commissioni_banca")
        RegolamentoCommissione regolamentoCommissioneBanca,

        /// Importo del bonifico (ISO 20022: {@code IntrBkSttlmAmt})
        BigDecimal importo,

        /// Divisa del bonifico (ISO 20022: {@code IntrBkSttlmAmt/Ccy})
        String divisa,

        /// Valore del cambio applicato
        @Column("valore_cambio")
        BigDecimal valoreCambio,

        /// Importo effettivamente addebitato
        @Column("importo_di_addebito")
        BigDecimal importoDiAddebito,

        /// Codice della causale della transazione
        @Column("codice_causale_transazione")
        String codiceCausaleTransazione,

        /// Identificativo alternativo del pagamento (ISO 20022: {@code RltdRmtInf/RmtId})
        @Column("altro_identificativo_pagamento")
        String altroIdentificativoPagamento

) implements DettaglioBonificoCommon {
    public static final class Entity
            implements com.flowpay.ccp.persistence.Entity<DettaglioBonificoAccountToAccount, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<DettaglioBonificoAccountToAccount> entityClass() {
            return DettaglioBonificoAccountToAccount.class;
        }
    }

    public static final class Repository
            extends com.flowpay.ccp.persistence.Repository<DettaglioBonificoAccountToAccount> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, DettaglioBonificoAccountToAccount> decoder) {
            super(client, decoder);
        }

        public Uni<DettaglioBonificoAccountToAccount> getByBonificoExtraSepa(UUID idBonificoExtraSepa) {
            return singleOrNull("SELECT * FROM dettaglio_bonifico_account_to_account WHERE id_bonifico_extra_sepa = $1",
                    Tuple.of(idBonificoExtraSepa));
        }

        public Uni<DettaglioBonificoAccountToAccount> getById(UUID id) {
            return single("SELECT * FROM dettaglio_bonifico_account_to_account WHERE id = $1", Tuple.of(id));
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
        public Collection<InformazioniNdg.WithLinkedEntities> informazioniNdg;

        @Override
        public DettaglioBonificoAccountToAccount getEntity() {
            return DettaglioBonificoAccountToAccount.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(bonificoExtraSepa);
            if (informazioniNdg != null)
                informazioniNdg.forEach(consumer);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.bonificoExtraSepa == null) {
                multis.add(new BonificoExtraSepa.Entity()
                        .repository(sqlClient).getById(idBonificoExtraSepa())
                        .map(linked -> {
                            this.bonificoExtraSepa = linked.withLinkedEntities();
                            this.bonificoExtraSepa.dettaglioBonificoAccountToAccount = this;
                            return this.bonificoExtraSepa;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.bonificoExtraSepa));
            }

            if (this.informazioniNdg == null) {
                this.informazioniNdg = new ArrayList<>();
                multis.add(new InformazioniNdg.Entity()
                        .repository(sqlClient).getAllByDettaglioBonifico(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.dettaglioBonificoAccountToAccount = this;
                            this.informazioniNdg.add(withLinked);
                            return withLinked;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class));
            } else {
                multis.add(Multi.createFrom().iterable(this.informazioniNdg).onItem()
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

    @Override
    public BigDecimal cambio() {
        return valoreCambio();
    }
}