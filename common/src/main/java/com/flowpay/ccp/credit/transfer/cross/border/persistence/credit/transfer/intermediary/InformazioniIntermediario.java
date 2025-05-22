package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.address.IndirizzoPostale;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.EnumKind;
import com.flowpay.ccp.persistence.Table;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Rappresenta le informazioni relative a un intermediario finanziario,
 * secondo lo schema ISO 20022 {@code FinancialInstitutionIdentification18}.<p>
 *
 * Questa classe memorizza i dettagli di un'istituzione finanziaria intermediaria
 * coinvolta in un pagamento internazionale, come il codice BIC, il nome dell'istituto,
 * l'identificativo del rapporto e altri identificativi utilizzati nei pagamenti.<p>
 *
 * La tabella associata nel database è {@code informazioni_intermediario}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 *       CREATE TABLE informazioni_intermediario (
 *       id UUID PRIMARY KEY,
 *       id_bonifico_extra_sepa UUID NOT NULL,
 *       id_info_rapporto UUID NOT NULL,
 *       id_indirizzo_postale UUID,
 *       tipo_intermediario tipo_intermediario NOT NULL,
 *       bic VARCHAR(11),
 *       intestazione VARCHAR(255),
 *       codice_lei VARCHAR(20),
 *       codice_sistema_clearing VARCHAR(255),
 *       identificativo_clearing VARCHAR(255),
 *
 *       -- Vincoli di chiave esterna
 *       CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
 *       REFERENCES bonifico_extra_sepa(id),
 *
 *       CONSTRAINT fk_info_rapporto FOREIGN KEY (id_info_rapporto)
 *       REFERENCES informazioni_rapporto(id),
 *
 *       CONSTRAINT fk_indirizzo_postale FOREIGN KEY (id_indirizzo_postale)
 *       REFERENCES indirizzo_postale(id)
 *       );
 * }</pre>
 *
 * @param id                       Identificativo univoco del record.<p>
 * @param idBonificoExtraSepa       Identificativo del bonifico extra SEPA associato.<p>Non mappato direttamente in FinancialInstitutionIdentification18.</p>
 * @param idInfoRapporto            Identificativo del rapporto informativo associato.<p>Non mappato direttamente in FinancialInstitutionIdentification18.</p>
 * @param idIndirizzoPostale        Identificativo dell'indirizzo postale associato.<p>Mappato in {@code PstlAdr} di FinancialInstitutionIdentification18.</p>
 * @param tipoIntermediario         Tipo di intermediario finanziario.<p>Gestito internamente, non mappato direttamente in FinancialInstitutionIdentification18.</p>
 * @param bic                       Codice BIC dell'istituto finanziario.<p>Mappato in {@code BICFI} di FinancialInstitutionIdentification18.</p>
 * @param intestazione              Nome dell'istituzione finanziaria.<p>Mappato in {@code Nm} di FinancialInstitutionIdentification18.</p>
 * @param codiceLEI                 Identificativo LEI dell'istituto finanziario.<p>Mappato in {@code LEI} di FinancialInstitutionIdentification18.</p>
 * @param codiceSistemaClearing      Codice identificativo di compensazione dell'istituto finanziario.<p>Mappato in {@code ClrSysMmbId/ClrSysId/Cd} di FinancialInstitutionIdentification18.</p>
 * @param identificativoClearing     Schema di compensazione utilizzato.<p>Mappato in {@code ClrSysMmbId/MmbId} di FinancialInstitutionIdentification18.</p>
 *
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("informazioni_intermediario")
public record InformazioniIntermediario(

        /// Identificativo univoco del record.
        UUID id,

        @Column("intermediario_documento_collegato")
        Boolean intermediarioDocumentoCollegato,

        /// Identificativo del bonifico extra SEPA associato.
        /// <p>Non mappato direttamente in FinancialInstitutionIdentification18.</p>
        @Column("id_bonifico_extra_sepa")
        UUID idBonificoExtraSepa,

        /// Identificativo del rapporto informativo associato.
        /// <p>Non mappato direttamente in FinancialInstitutionIdentification18.</p>
        @Column("id_info_rapporto")
        UUID idInfoRapporto,

        /// Identificativo dell'indirizzo postale associato.
        /// <p>Mappato in `PstlAdr` di FinancialInstitutionIdentification18.</p>
        @Column("id_indirizzo_postale")
        UUID idIndirizzoPostale,

        /// Tipo di intermediario finanziario.
        /// <p>Gestito internamente, non mappato direttamente in FinancialInstitutionIdentification18.</p>
        @Column("tipo_intermediario")
        @EnumKind
        TipoIntermediario tipoIntermediario,

        /// Codice BIC dell'istituto finanziario.
        /// <p>Mappato in `BICFI` di FinancialInstitutionIdentification18.</p>
        String bic,

        /// Nome dell'istituzione finanziaria.
        /// <p>Mappato in `Nm` di FinancialInstitutionIdentification18.</p>
        String intestazione,

        /// Identificativo LEI dell'istituto finanziario.
        /// <p>Mappato in `LEI` di FinancialInstitutionIdentification18.</p>
        @Column("codice_lei")
        String codiceLEI,

        /// Codice identificativo di compensazione dell'istituto finanziario.
        /// <p>Mappato in `ClrSysMmbId/ClrSysId/Cd` di FinancialInstitutionIdentification18.</p>
        @Column("codice_sistema_clearing")
        String codiceSistemaClearing,

        /// Schema di compensazione utilizzato.
        /// <p>Mappato in `ClrSysMmbId/MmbId` di FinancialInstitutionIdentification18.</p>
        @Column("identificativo_clearing")
        String identificativoClearing
) {

    private static final Logger logger = Logger.getLogger(InformazioniIntermediario.class);

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<InformazioniIntermediario, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<InformazioniIntermediario> entityClass() {
            return InformazioniIntermediario.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<InformazioniIntermediario> {

        public Repository(SqlClient client, java.util.function.Function<io.vertx.mutiny.sqlclient.Row, InformazioniIntermediario> decoder) {
            super(client, decoder);
        }

        public Multi<InformazioniIntermediario> getAllByBonificoExtraSepa(UUID idBonificoExtraSepa) {
            return multi("SELECT * FROM informazioni_intermediario WHERE id_bonifico_extra_sepa = $1", Tuple.of(idBonificoExtraSepa));
        }

        public Uni<Optional<InformazioniIntermediario>> getByBonificoExtraSepaAndKind(UUID idBonificoExtraSepa, TipoIntermediario tipoIntermediario, Boolean documentoCollegato) {
            return singleOrOptional("""
                SELECT * FROM informazioni_intermediario
                WHERE
                    id_bonifico_extra_sepa = $1
                    AND tipo_intermediario = $2
                    AND intermediario_documento_collegato = $3
            """, Tuple.of(idBonificoExtraSepa, tipoIntermediario, documentoCollegato));
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
        public InformazioniRapporto.WithLinkedEntities informazioniRapporto;
        public IndirizzoPostale.WithLinkedEntities indirizzoPostale;

        @Override
        public InformazioniIntermediario getEntity() {
            return InformazioniIntermediario.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(bonificoExtraSepa);
            consumer.accept(informazioniRapporto);
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

            if (this.informazioniRapporto == null) {
                multis.add(new InformazioniRapporto.Entity()
                        .repository(sqlClient).getById(idInfoRapporto())
                        .map(linked -> {
                            if (linked != null) {
                                this.informazioniRapporto = linked.withLinkedEntities();
                                return this.informazioniRapporto;
                            }
                            return null;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.informazioniRapporto));
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
        }}
}