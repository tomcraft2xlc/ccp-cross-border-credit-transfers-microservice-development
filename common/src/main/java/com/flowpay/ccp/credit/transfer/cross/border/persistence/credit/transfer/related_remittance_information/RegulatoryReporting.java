package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.Table;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Rappresenta i dettagli del Regulatory Reporting relativi a una transazione di pagamento,<p>
 * secondo lo schema ISO 20022 {@code RgltryRptg}.<p>
 *
 * Questa classe memorizza informazioni regolamentari, inclusi il tipo di reporting,
 * l'autorità richiedente e il paese dell'autorità richiedente.<p>
 *
 * La tabella associata nel database è {@code regulatory_reporting}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE regulatory_reporting (
 *     id UUID PRIMARY KEY,
 *     id_bonifico_extra_sepa UUID NOT NULL,
 *     tipo VARCHAR(255),
 *     autorita_richiedente VARCHAR(255),
 *     paese_autorita_richiedente VARCHAR(2),
 *     CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
 *         REFERENCES bonifico_extra_sepa(id)
 * );
 * }</pre>
 *
 * @param id                          Identificativo univoco del record.<p>
 * @param idBonificoExtraSepa          Identificativo del bonifico extra SEPA associato.<p>
 * @param tipo                         Tipo di reporting regolamentare.<p>Mappato in {@code RgltryRptg/DbtCdtRptgInd}.</p>
 * @param autoritaRichiedente          Nome dell'autorità regolamentare richiedente.<p>Mappato in {@code RgltryRptg/Authrty/Nm}.</p>
 * @param paeseAutoritaRichiedente     Paese dell'autorità regolamentare richiedente.<p>Mappato in {@code RgltryRptg/Authrty/Ctry}.</p>
 *
 * @see BonificoExtraSepa
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("regulatory_reporting")
public record RegulatoryReporting(

        /// Identificativo univoco del record.
        UUID id,

        /// Identificativo del bonifico extra SEPA associato.
        @Column("id_bonifico_extra_sepa")
        UUID idBonificoExtraSepa,

        /// Tipo di reporting regolamentare.
        /// <p>Mappato in `RgltryRptg/DbtCdtRptgInd`.</p>
        TipoDiRegulatoryReporting tipo,

        /// Nome dell'autorità regolamentare richiedente.
        /// <p>Mappato in `RgltryRptg/Authrty/Nm`.</p>
        @Column("autorita_richiedente")
        String autoritaRichiedente,

        /// Paese dell'autorità regolamentare richiedente.
        /// <p>Mappato in `RgltryRptg/Authrty/Ctry`.</p>
        @Column("paese_autorita_richiedente")
        String paeseAutoritaRichiedente
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<RegulatoryReporting, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<RegulatoryReporting> entityClass() {
            return RegulatoryReporting.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<RegulatoryReporting> {

        public Repository(SqlClient client, java.util.function.Function<io.vertx.mutiny.sqlclient.Row, RegulatoryReporting> decoder) {
            super(client, decoder);
        }

        public Multi<RegulatoryReporting> getAllByBonificoExtraSepa(UUID idBonificoExtraSepa) {
            return multi("SELECT * FROM regulatory_reporting WHERE id_bonifico_extra_sepa = $1", Tuple.of(idBonificoExtraSepa));
        }

        public Uni<RegulatoryReporting> getById(UUID id) {
            return single("SELECT * FROM regulatory_reporting WHERE id = $1", Tuple.of(id));
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
        public Collection<DettagliRegulatoryReporting.WithLinkedEntities> dettagliRegulatoryReportings;
        

        @Override
        public RegulatoryReporting getEntity() {
            return RegulatoryReporting.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(bonificoExtraSepa);
            if (dettagliRegulatoryReportings != null) dettagliRegulatoryReportings.forEach(consumer);
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

            if (this.dettagliRegulatoryReportings == null) {
                this.dettagliRegulatoryReportings = new ArrayList<>();
                multis.add(new DettagliRegulatoryReporting.Entity()
                        .repository(sqlClient).getAllByRegulatoryReporting(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.regulatoryReporting = this;
                            this.dettagliRegulatoryReportings.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.dettagliRegulatoryReportings).onItem()
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