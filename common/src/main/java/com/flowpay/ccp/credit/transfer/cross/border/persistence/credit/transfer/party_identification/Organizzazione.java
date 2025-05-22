package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.Table;
import com.prowidesoftware.swift.model.mx.dic.OrganisationIdentification29;
import com.prowidesoftware.swift.model.mx.dic.Party38Choice;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Rappresenta le informazioni relative a un'organizzazione coinvolta nella transazione di pagamento,
 * mappata secondo lo schema ISO 20022 {@code Party38Choice/OrganisationIdentification29}.<p>
 *
 * Questa classe memorizza i dettagli identificativi di un'organizzazione,
 * tra cui il codice BIC, il codice LEI e altri identificativi univoci.<p>
 *
 * La tabella associata nel database è {@code organizzazione}.<p>
 *
 * I campi di questa classe sono mappati su {@code OrganisationIdentification29}
 * all'interno di {@code Party38Choice} di ISO 20022.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE organizzazione (
 *     id UUID PRIMARY KEY,
 *     id_informazioni_attore UUID NOT NULL,
 *     bic VARCHAR(11),
 *     codice_lei VARCHAR(20),
 *     identificativo_organizzazione_1 VARCHAR(255),
 *     codice_identificativo_organizzazione_1 VARCHAR(255),
 *     codice_proprietario_identificativo_organizzazione_1 VARCHAR(255),
 *     emittente_1 VARCHAR(255),
 *     identificativo_organizzazione_2 VARCHAR(255),
 *     codice_identificativo_organizzazione_2 VARCHAR(255),
 *     codice_proprietario_identificativo_organizzazione_2 VARCHAR(255),
 *     emittente_2 VARCHAR(255),
 *     CONSTRAINT fk_informazioni_attore FOREIGN KEY (id_informazioni_attore)
 *         REFERENCES informazioni_attore(id)
 * );
 * }</pre>
 *
 * @param id                                Identificativo univoco del record.<p>
 * @param idInformazioniAttore              Identificativo delle informazioni dell'attore.<p>
 * @param bic                               Codice BIC dell'organizzazione.<p>Mappato in {@code AnyBIC} di OrganisationIdentification29.</p>
 * @param codiceLEI                         Codice LEI dell'organizzazione.<p>Mappato in {@code LEI} di OrganisationIdentification29.</p>
 * @param identificativoOrganizzazione1      Identificativo alternativo dell'organizzazione.<p>Mappato in {@code Othr/Id} di OrganisationIdentification29.</p>
 * @param codiceIdentificativoOrganizzazione1 Schema dell'identificativo alternativo.<p>Mappato in {@code Othr/SchemeNm/Cd} di OrganisationIdentification29.</p>
 * @param codiceProprietarioIdentificativoOrganizzazione1 Proprietario dello schema identificativo alternativo.<p>Mappato in {@code Othr/SchemeNm/Prtry} di OrganisationIdentification29.</p>
 * @param emittente1                         Emittente dell'identificativo alternativo.<p>Mappato in {@code Othr/Issr} di OrganisationIdentification29.</p>
 * @param identificativoOrganizzazione2      Secondo identificativo alternativo dell'organizzazione.<p>Mappato in {@code Othr/Id} di OrganisationIdentification29.</p>
 * @param codiceIdentificativoOrganizzazione2 Schema del secondo identificativo alternativo.<p>Mappato in {@code Othr/SchemeNm/Cd} di OrganisationIdentification29.</p>
 * @param codiceProprietarioIdentificativoOrganizzazione2 Proprietario del secondo schema identificativo alternativo.<p>Mappato in {@code Othr/SchemeNm/Prtry} di OrganisationIdentification29.</p>
 * @param emittente2                         Emittente del secondo identificativo alternativo.<p>Mappato in {@code Othr/Issr} di OrganisationIdentification29.</p>
 *
 * @see InformazioniAttore
 * @see OrganisationIdentification29
 * @see Party38Choice
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("organizzazione")
public record Organizzazione(

        /// Identificativo univoco del record.
        UUID id,

        /// Identificativo delle informazioni dell'attore.
        @Column("id_informazioni_attore")
        UUID idInformazioniAttore,

        /// Codice BIC dell'organizzazione.
        /// <p>Mappato in `AnyBIC` di OrganisationIdentification29.</p>
        String bic,

        /// Codice LEI dell'organizzazione.
        /// <p>Mappato in `LEI` di OrganisationIdentification29.</p>
        @Column("codice_lei")
        String codiceLEI,

        /// Identificativo alternativo dell'organizzazione.
        /// <p>Mappato in `Othr/Id` di OrganisationIdentification29.</p>
        @Column("identificativo_organizzazione_1")
        String identificativoOrganizzazione1,

        /// Schema dell'identificativo alternativo.
        /// <p>Mappato in `Othr/SchemeNm/Cd` di OrganisationIdentification29.</p>
        @Column("codice_identificativo_organizzazione_1")
        String codiceIdentificativoOrganizzazione1,

        /// Proprietario dello schema identificativo alternativo.
        /// <p>Mappato in `Othr/SchemeNm/Prtry` di OrganisationIdentification29.</p>
        @Column("codice_proprietario_identificativo_organizzazione_1")
        String codiceProprietarioIdentificativoOrganizzazione1,

        /// Emittente dell'identificativo alternativo.
        /// <p>Mappato in `Othr/Issr` di OrganisationIdentification29.</p>
        @Column("emittente_1")
        String emittente1,

        /// Secondo identificativo alternativo dell'organizzazione.
        /// <p>Mappato in `Othr/Id` di OrganisationIdentification29.</p>
        @Column("identificativo_organizzazione_2")
        String identificativoOrganizzazione2,

        /// Schema del secondo identificativo alternativo.
        /// <p>Mappato in `Othr/SchemeNm/Cd` di OrganisationIdentification29.</p>
        @Column("codice_identificativo_organizzazione_2")
        String codiceIdentificativoOrganizzazione2,

        /// Proprietario del secondo schema identificativo alternativo.
        /// <p>Mappato in `Othr/SchemeNm/Prtry` di OrganisationIdentification29.</p>
        @Column("codice_proprietario_identificativo_organizzazione_2")
        String codiceProprietarioIdentificativoOrganizzazione2,

        /// Emittente del secondo identificativo alternativo.
        /// <p>Mappato in `Othr/Issr` di OrganisationIdentification29.</p>
        @Column("emittente_2")
        String emittente2
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<Organizzazione, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<Organizzazione> entityClass() {
            return Organizzazione.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<Organizzazione> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, Organizzazione> decoder) {
            super(client, decoder);
        }

        public Uni<Organizzazione> getByInformazioniAttore(UUID idInformazioniAttore) {
            return singleOrNull("SELECT * FROM organizzazione WHERE id_informazioni_attore = $1",
                    Tuple.of(idInformazioniAttore));
        }

    }

    /**
     * Prepara a recuperare le entity linkate.
     */
    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {
        public InformazioniAttore.WithLinkedEntities informazioniAttore;

        @Override
        public Organizzazione getEntity() {
            return Organizzazione.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(informazioniAttore);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.informazioniAttore == null) {
                multis.add(new InformazioniAttore.Entity()
                        .repository(sqlClient).getById(id())
                        .map(linked -> {
                            this.informazioniAttore = linked.withLinkedEntities();
                            this.informazioniAttore.organizzazione = this;
                            return this.informazioniAttore;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.informazioniAttore));
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