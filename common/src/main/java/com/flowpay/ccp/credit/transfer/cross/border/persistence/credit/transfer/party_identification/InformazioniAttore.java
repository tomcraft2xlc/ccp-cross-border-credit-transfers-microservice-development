package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.address.IndirizzoPostale;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.Table;
import com.prowidesoftware.swift.model.mx.dic.PartyIdentification135;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Rappresenta le informazioni relative a un attore coinvolto nella transazione di pagamento,
 * mappato secondo lo schema ISO 20022 {@code PartyIdentification135}.
 * <p>
 * Questa classe memorizza i dettagli identificativi di una persona o di un'organizzazione,
 * compresi il nome, l'indirizzo postale, il codice identificativo e i dettagli di contatto.
 * </p>
 * <p>
 * La tabella associata nel database è {@code informazioni_attore}.
 * </p>
 * <p>
 * I campi di questa classe sono mappati su {@code PartyIdentification135} di ISO 20022.
 * </p>
 *
 * @param id                  Identificativo univoco del record.
 * @param idBonificoExtraSepa Identificativo del bonifico extra SEPA.
 * @param idInfoRapporto      Identificativo del rapporto informativo.
 * @param idIndirizzoPostale  Identificativo dell'indirizzo postale associato. <p>Mappato in {@code PstlAdr} di PartyIdentification135.</p>
 * @param tipo                Tipo dell'attore coinvolto nella transazione.
 * @param intestazione        Nome dell'attore. <p>Mappato in {@code Nm} di PartyIdentification135.</p>
 * @param paeseDiResidenza    Paese di residenza dell'attore. <p>Mappato in {@code CtryOfRes} di PartyIdentification135.</p>
 *
 * @see BonificoExtraSepa
 * @see InformazioniRapporto
 * @see IndirizzoPostale
 * @see TipoAttore
 * @see PartyIdentification135
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("informazioni_attore")
public record InformazioniAttore(

        /// Identificativo univoco del record.
        UUID id,

        /// Identificativo del bonifico extra SEPA.
        @Column("id_bonifico_extra_sepa")
        UUID idBonificoExtraSepa,

        /// Identificativo del rapporto informativo.
        @Column("id_info_rapporto")
        UUID idInfoRapporto,

        /// Identificativo dell'indirizzo postale associato.
        /// <p>Mappato in `PstlAdr` di PartyIdentification135.</p>
        @Column("id_indirizzo_postale")
        UUID idIndirizzoPostale,

        /// Tipo dell'attore coinvolto nella transazione.
        @Column("tipo_attore")
        TipoAttore tipo,

        /// Nome dell'attore.
        /// <p>Mappato in `Nm` di PartyIdentification135.</p>
        String intestazione,

        /// Paese di residenza dell'attore.
        /// <p>Mappato in `CtryOfRes` di PartyIdentification135.</p>
        @Column("paese_di_residenza")
        String paeseDiResidenza
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<InformazioniAttore, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<InformazioniAttore> entityClass() {
            return InformazioniAttore.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<InformazioniAttore> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, InformazioniAttore> decoder) {
            super(client, decoder);
        }

        public Uni<InformazioniAttore> getById(UUID id) {
            return single("SELECT * FROM informazioni_attore WHERE id = $1", Tuple.of(id));
        }

        public Multi<InformazioniAttore> getAllByBonificoExtraSepa(UUID idBonificoExtraSepa) {
            return multi("SELECT * FROM informazioni_attore WHERE id_bonifico_extra_sepa = $1",
                    Tuple.of(idBonificoExtraSepa));
        }

        public Uni<Optional<InformazioniAttore>> getByBonificoExtraSepaAndKind(UUID idBonificoExtraSepa, TipoAttore tipo) {
            return singleOrOptional("""
                SELECT * 
                FROM informazioni_attore 
                WHERE 
                    id_bonifico_extra_sepa = $1 
                    AND tipo_attore = $2""",
                    Tuple.of(idBonificoExtraSepa, tipo));
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
        public Organizzazione.WithLinkedEntities organizzazione;
        public Privato.WithLinkedEntities privato;

        @Override
        public InformazioniAttore getEntity() {
            return InformazioniAttore.this;
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
            consumer.accept(organizzazione);
            consumer.accept(privato);
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

            if (this.organizzazione == null) {
                multis.add(new Organizzazione.Entity()
                        .repository(sqlClient).getByInformazioniAttore(id())
                        .map(linked -> {
                            if (linked == null) {
                                return null;
                            }
                            this.organizzazione = linked.withLinkedEntities();
                            this.organizzazione.informazioniAttore = this;
                            return this.organizzazione;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.organizzazione));
            }

            if (this.privato == null) {
                multis.add(new Privato.Entity()
                        .repository(sqlClient).getByInformazioniAttore(id())
                        .map(linked -> {
                            if (linked == null) {
                                return null;
                            }
                            this.privato = linked.withLinkedEntities();
                            this.privato.informazioniAttore = this;
                            return this.privato;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.privato));
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