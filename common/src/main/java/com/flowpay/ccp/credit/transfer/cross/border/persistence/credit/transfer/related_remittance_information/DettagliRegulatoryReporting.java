package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information;

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
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Rappresenta i dettagli del Regulatory Reporting relativi a una transazione di pagamento,<p>
 * secondo lo schema ISO 20022 {@code RgltryRptg/Authrty/Dtls}.<p>
 *
 * Questa classe memorizza informazioni regolamentari, inclusi il tipo di dettaglio,
 * la data dell'evento regolamentare, l'importo e la divisa.<p>
 *
 * La tabella associata nel database è {@code dettagli_regulatory_reporting}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE dettagli_regulatory_reporting (
 *     id UUID PRIMARY KEY,
 *     id_regulatory_reporting UUID NOT NULL,
 *     dettaglio VARCHAR(255),
 *     data DATE,
 *     importo NUMERIC(18,2),
 *     divisa VARCHAR(3),
 *     paese VARCHAR(2),
 *     informazioni_aggiuntive TEXT,
 *     CONSTRAINT fk_regulatory_reporting FOREIGN KEY (id_regulatory_reporting)
 *         REFERENCES regulatory_reporting(id)
 * );
 * }</pre>
 *
 * @param id                        Identificativo univoco del record.<p>
 * @param idRegulatoryReporting      Identificativo del regulatory reporting associato.<p>
 * @param dettaglio                  Tipo di dettaglio regolamentare.<p>Mappato in {@code RgltryRptg/Authrty/Dtls/Tp}.</p>
 * @param data                       Data dell'evento regolamentare.<p>Mappato in {@code RgltryRptg/Authrty/Dtls/Dt}.</p>
 * @param importo                    Importo associato all'evento regolamentare.<p>Mappato in {@code RgltryRptg/Authrty/Dtls/Amt}.</p>
 * @param divisa                     Divisa dell'importo regolamentare.<p>Mappato in {@code RgltryRptg/Authrty/Dtls/Ccy}.</p>
 * @param paese                      Paese associato all'evento regolamentare.<p>Mappato in {@code RgltryRptg/Authrty/Dtls/Ctry}.</p>
 * @param informazioniAggiuntive      Ulteriori informazioni regolamentari.<p>Mappato in {@code RgltryRptg/Authrty/Dtls/Inf}.</p>
 *
 * @see RegulatoryReporting
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("dettagli_regulatory_reporting")
public record DettagliRegulatoryReporting(

        /// Identificativo univoco del record.
        UUID id,

        /// Identificativo del regulatory reporting associato.
        @Column("id_regulatory_reporting")
        UUID idRegulatoryReporting,

        /// Tipo di dettaglio regolamentare.
        /// <p>Mappato in `RgltryRptg/Authrty/Dtls/Tp`.</p>
        String dettaglio,

        /// Data dell'evento regolamentare.
        /// <p>Mappato in `RgltryRptg/Authrty/Dtls/Dt`.</p>
        @DateKind(DateKind.DateKindEnum.DATE) 
        LocalDate data,

        /// Importo associato all'evento regolamentare.
        /// <p>Mappato in `RgltryRptg/Authrty/Dtls/Amt`.</p>
        BigDecimal importo,

        /// Divisa dell'importo regolamentare.
        /// <p>Mappato in `RgltryRptg/Authrty/Dtls/Ccy`.</p>
        String divisa,

        /// Paese associato all'evento regolamentare.
        /// <p>Mappato in `RgltryRptg/Authrty/Dtls/Ctry`.</p>
        String paese,

        /// Ulteriori informazioni regolamentari.
        /// <p>Mappato in `RgltryRptg/Authrty/Dtls/Inf`.</p>
        @Column("informazioni_aggiuntive")
        String informazioniAggiuntive
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<DettagliRegulatoryReporting, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<DettagliRegulatoryReporting> entityClass() {
            return DettagliRegulatoryReporting.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<DettagliRegulatoryReporting> {

        public Repository(SqlClient client, java.util.function.Function<io.vertx.mutiny.sqlclient.Row, DettagliRegulatoryReporting> decoder) {
            super(client, decoder);
        }

        public Multi<DettagliRegulatoryReporting> getAllByRegulatoryReporting(UUID idRegulatoryReporting) {
            return multi("SELECT * FROM dettagli_regulatory_reporting WHERE id_regulatory_reporting = $1", Tuple.of(idRegulatoryReporting));
        }

    }
    
    /**
     * Prepara a recuperare le entity linkate.
     */
    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {
        public RegulatoryReporting.WithLinkedEntities regulatoryReporting;
        

        @Override
        public DettagliRegulatoryReporting getEntity() {
            return DettagliRegulatoryReporting.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(regulatoryReporting);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.regulatoryReporting == null) {
                multis.add(new RegulatoryReporting.Entity()
                        .repository(sqlClient).getById(idRegulatoryReporting())
                        .map(linked -> {
                            this.regulatoryReporting = linked.withLinkedEntities();
                            // Adding this to the parent would not be so beneficial as 
                            // the sibling would need to be loaded, and loading is 
                            // done en-masse.
                            return this.regulatoryReporting;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.regulatoryReporting));
            }

            return Multi.createBy().merging().streams(multis);
        }

        @Override
        public Uni<Void> insert(SqlClient sqlClient) {
            var entity = new Entity();
            return entity.repository(sqlClient).run(entity.insert(getEntity()));
        }}
}