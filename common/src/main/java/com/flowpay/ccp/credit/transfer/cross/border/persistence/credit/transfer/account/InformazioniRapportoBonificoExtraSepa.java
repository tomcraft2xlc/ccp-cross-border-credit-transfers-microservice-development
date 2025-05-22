package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
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
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Rappresenta le informazioni relative al rapporto di un bonifico extra SEPA,
 * in conformità con lo schema ISO 20022.
 * <p>
 * Questa classe memorizza i dettagli dell'associazione tra un bonifico extra SEPA e un rapporto specifico,
 * come ad esempio l'account del debitore (`DebtorAccount`).
 * <p>
 * Tabella associata: {@code informazioni_rapporto_bonifico_extra_sepa}.
 * <p>
 * SQL:
 * <pre>{@code
 * CREATE TABLE informazioni_rapporto_bonifico_extra_sepa (
 *     id UUID PRIMARY KEY,
 *     id_info_rapporto UUID NOT NULL,
 *     id_bonifico_extra_sepa UUID NOT NULL,
 *     tipo_informazioni_rapporto tipo_informazioni_rapporto NOT NULL,
 *
 *     -- Vincoli di chiave esterna
 *     CONSTRAINT fk_info_rapporto FOREIGN KEY (id_info_rapporto)
 *         REFERENCES informazioni_rapporto(id),
 *
 *     CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
 *         REFERENCES bonifico_extra_sepa(id)
 * );
 * }</pre>
 * @param id Identificativo univoco del record.
 * @param idInfoRapporto Identificativo del rapporto informativo associato.
 * @param idBonificoExtraSepa Identificativo del bonifico extra SEPA associato.
 * @param tipoInformazioniRapporto Indica il tipo di rapporto, mappato in un campo corrispondente del messaggio ISO 20022,
 *                                 ad esempio `DebtorAccount`.
 *
 * @see TipoInformazioniRapporto
 */
@Table("informazioni_rapporto_bonifico_extra_sepa")
public record InformazioniRapportoBonificoExtraSepa(

        /// Identificativo univoco del record.
        UUID id,

        /// Identificativo del rapporto informativo associato.
        /// Questo valore collega il rapporto informativo a una specifica transazione.
        @Column("id_info_rapporto")
        UUID idInfoRapporto,

        /// Identificativo del bonifico extra SEPA associato.
        /// Questo valore permette di associare il rapporto a un bonifico internazionale.
        @Column("id_bonifico_extra_sepa")
        UUID idBonificoExtraSepa,

        /// Tipo di informazioni relative al rapporto.
        /// Questo campo è mappato nel messaggio ISO 20022 nel campo corrispondente,
        /// ad esempio `DebtorAccount` per rappresentare l'account del debitore.
        @Column("tipo_informazioni_rapporto")
        @EnumKind
        TipoInformazioniRapporto tipoInformazioniRapporto
) {

    private static final Logger log = Logger.getLogger(InformazioniRapportoBonificoExtraSepa.class);

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<InformazioniRapportoBonificoExtraSepa, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<InformazioniRapportoBonificoExtraSepa> entityClass() {
            return InformazioniRapportoBonificoExtraSepa.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<InformazioniRapportoBonificoExtraSepa> {

        public Repository(SqlClient client, java.util.function.Function<io.vertx.mutiny.sqlclient.Row, InformazioniRapportoBonificoExtraSepa> decoder) {
            super(client, decoder);
        }

        public Multi<InformazioniRapportoBonificoExtraSepa> getAllByBonificoExtraSepa(UUID idBonificoExtraSepa) {
            return multi("SELECT * FROM informazioni_rapporto_bonifico_extra_sepa WHERE id_bonifico_extra_sepa = $1", Tuple.of(idBonificoExtraSepa));
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

        @Override
        public InformazioniRapportoBonificoExtraSepa getEntity() {
            return InformazioniRapportoBonificoExtraSepa.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(bonificoExtraSepa);
            consumer.accept(informazioniRapporto);
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
                log.debug("loading informazioni rapporto my id:" + id() + " id rapporto: " + idInfoRapporto());
                multis.add(new InformazioniRapporto.Entity()
                        .repository(sqlClient).getById(idInfoRapporto())
                        .map(linked -> {
                            log.debug("rapporto trovato: " + (linked != null));
                            if (linked != null) {
                                this.informazioniRapporto = linked.withLinkedEntities();
                                return this.informazioniRapporto;
                            } else {
                                return null;
                            }
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.informazioniRapporto));
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