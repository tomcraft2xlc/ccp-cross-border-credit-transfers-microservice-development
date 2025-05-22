package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.address.IndirizzoPostale;
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
 * Rappresenta i riferimenti aggiuntivi relativi a un pagamento,<p>
 * secondo lo schema ISO 20022 {@code RltdRmtInf/RmtLctnDtls}.<p>
 *
 * Questa classe memorizza informazioni aggiuntive relative al pagamento, tra cui la modalità
 * di avviso, i dettagli del destinatario del reporting e l'indirizzo postale associato.<p>
 *
 * La tabella associata nel database è {@code riferimenti_aggiuntivi_pagamento}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE riferimenti_aggiuntivi_pagamento (
 *     id UUID PRIMARY KEY,
 *     id_bonifico_extra_sepa UUID NOT NULL,
 *     modalita_avviso_pagamento VARCHAR(255),
 *     email_destinatario_reporting VARCHAR(255),
 *     intestazione_destinatario_reporting VARCHAR(255),
 *     id_indirizzo_postale UUID,
 *     CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
 *         REFERENCES bonifico_extra_sepa(id),
 *     CONSTRAINT fk_indirizzo_postale FOREIGN KEY (id_indirizzo_postale)
 *         REFERENCES indirizzo_postale(id)
 * );
 * }</pre>
 *
 * @param id                               Identificativo univoco del record.<p>
 * @param idBonificoExtraSepa              Identificativo del bonifico extra SEPA associato.<p>
 * @param modalitaAvvisoPagamento          Modalità di avviso del pagamento.<p>Mappato in {@code RltdRmtInf/RmtLctnDtls/Mtd}.</p>
 * @param emailDestinatarioReporting       Indirizzo email del destinatario del reporting.<p>Mappato in {@code RltdRmtInf/RmtLctnDtls/ElctrncAdr}.</p>
 * @param intestazioneDestinatarioReporting Nome del destinatario del reporting.<p>Mappato in {@code RltdRmtInf/RmtLctnDtls/PstlAdr/Nm}.</p>
 * @param idIndirizzoPostale               Identificativo dell'indirizzo postale associato.<p>Mappato in {@code RltdRmtInf/RmtLctnDtls/PstlAdr/Adr}.</p>
 *
 * @see BonificoExtraSepa
 * @see IndirizzoPostale
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("riferimenti_aggiuntivi_pagamento")
public record RiferimentiAggiuntiviPagamento(

        /// Identificativo univoco del record.
        UUID id,

        /// Identificativo del bonifico extra SEPA associato.
        @Column("id_bonifico_extra_sepa")
        UUID idBonificoExtraSepa,

        /// Modalità di avviso del pagamento.
        /// <p>Mappato in `RltdRmtInf/RmtLctnDtls/Mtd`.</p>
        @Column("modalita_avviso_pagamento")
        String modalitaAvvisoPagamento,

        /// Indirizzo email del destinatario del reporting.
        /// <p>Mappato in `RltdRmtInf/RmtLctnDtls/ElctrncAdr`.</p>
        @Column("email_destinatario_reporting")
        String emailDestinatarioReporting,

        /// Nome del destinatario del reporting.
        /// <p>Mappato in `RltdRmtInf/RmtLctnDtls/PstlAdr/Nm`.</p>
        @Column("intestazione_destinatario_reporting")
        String intestazioneDestinatarioReporting,

        /// Identificativo dell'indirizzo postale associato.
        /// <p>Mappato in `RltdRmtInf/RmtLctnDtls/PstlAdr/Adr`.</p>
        @Column("id_indirizzo_postale")
        UUID idIndirizzoPostale
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<RiferimentiAggiuntiviPagamento, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<RiferimentiAggiuntiviPagamento> entityClass() {
            return RiferimentiAggiuntiviPagamento.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<RiferimentiAggiuntiviPagamento> {

        public Repository(SqlClient client, java.util.function.Function<io.vertx.mutiny.sqlclient.Row, RiferimentiAggiuntiviPagamento> decoder) {
            super(client, decoder);
        }


        public Multi<RiferimentiAggiuntiviPagamento> getAllByBonificoExtraSepa(UUID idBonificoExtraSepa) {
            return multi("SELECT * FROM riferimenti_aggiuntivi_pagamento WHERE id_bonifico_extra_sepa = $1", Tuple.of(idBonificoExtraSepa));
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
        public IndirizzoPostale.WithLinkedEntities indirizzoPostale;

        @Override
        public RiferimentiAggiuntiviPagamento getEntity() {
            return RiferimentiAggiuntiviPagamento.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(bonificoExtraSepa);
            consumer.accept(indirizzoPostale);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.bonificoExtraSepa == null) {
                multis.add(new BonificoExtraSepa.Entity()
                        .repository(sqlClient).getById(idBonificoExtraSepa())
                        .map(linked -> {
                            this.bonificoExtraSepa = linked.withLinkedEntities();
                            // Adding this to the parent would not be so beneficial as
                            // the sibling would need to be loaded, and loading is
                            // done en-masse.
                            return this.bonificoExtraSepa;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.bonificoExtraSepa));
            }

            if (this.indirizzoPostale == null) {
                multis.add(new IndirizzoPostale.Entity()
                        .repository(sqlClient).getById(idIndirizzoPostale())
                        .map(linked -> {
                            if (linked == null) {
                                return null;
                            }
                            this.indirizzoPostale = linked.withLinkedEntities();
                            return this.bonificoExtraSepa;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.indirizzoPostale));
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