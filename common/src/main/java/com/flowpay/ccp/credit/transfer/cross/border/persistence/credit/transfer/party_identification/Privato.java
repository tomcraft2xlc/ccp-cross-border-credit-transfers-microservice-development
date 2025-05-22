package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.DateKind;
import com.flowpay.ccp.persistence.Table;
import com.prowidesoftware.swift.model.mx.dic.Party38Choice;
import com.prowidesoftware.swift.model.mx.dic.PersonIdentification13;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Rappresenta le informazioni relative a una persona fisica coinvolta nella transazione di pagamento,
 * mappata secondo lo schema ISO 20022 {@code Party38Choice/PersonIdentification13}.<p>
 *
 * Questa classe memorizza i dettagli identificativi di una persona,
 * tra cui la data e il luogo di nascita, nonché il codice identificativo e il suo emittente.<p>
 *
 * La tabella associata nel database è {@code privato}.<p>
 *
 * I campi di questa classe sono mappati su {@code PersonIdentification13}
 * all'interno di {@code Party38Choice} di ISO 20022.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE privato (
 *     id UUID PRIMARY KEY,
 *     id_informazioni_attore UUID NOT NULL,
 *     data_di_nascita DATE,
 *     provincia_di_nascita VARCHAR(255),
 *     citta_di_nascita VARCHAR(255),
 *     paese_di_nascita VARCHAR(2),
 *     identificativo_soggetto_1 VARCHAR(255),
 *     codice_identificativo_soggetto_1 VARCHAR(255),
 *     codice_proprietario_identificativo_soggetto_1 VARCHAR(255),
 *     emittente_1 VARCHAR(255),
 *     identificativo_soggetto_2 VARCHAR(255),
 *     codice_identificativo_soggetto_2 VARCHAR(255),
 *     codice_proprietario_identificativo_soggetto_2 VARCHAR(255),
 *     emittente_2 VARCHAR(255),
 *     CONSTRAINT fk_informazioni_attore FOREIGN KEY (id_informazioni_attore)
 *         REFERENCES informazioni_attore(id)
 * );
 * }</pre>
 *
 * @param id                                  Identificativo univoco del record.<p>
 * @param idInformazioniAttore                Identificativo delle informazioni dell'attore.<p>
 * @param dataDiNascita                       Data di nascita della persona.<p>Mappato in {@code DtAndPlcOfBirth/BirthDt} di PersonIdentification13.</p>
 * @param provinciaDiNascita                  Provincia di nascita della persona.<p>Mappato in {@code DtAndPlcOfBirth/PrvcOfBirth} di PersonIdentification13.</p>
 * @param cittaDiNascita                      Città di nascita della persona.<p>Mappato in {@code DtAndPlcOfBirth/CityOfBirth} di PersonIdentification13.</p>
 * @param paeseDiNascita                      Paese di nascita della persona.<p>Mappato in {@code DtAndPlcOfBirth/CtryOfBirth} di PersonIdentification13.</p>
 * @param identificativoSoggetto1             Identificativo univoco della persona.<p>Mappato in {@code Othr/Id} di PersonIdentification13.</p>
 * @param codiceIdentificativoSoggetto1       Schema dell'identificativo univoco della persona.<p>Mappato in {@code Othr/SchemeNm/Cd} di PersonIdentification13.</p>
 * @param codiceProprietarioIdentificativoSoggetto1 Proprietario dello schema identificativo.<p>Mappato in {@code Othr/SchemeNm/Prtry} di PersonIdentification13.</p>
 * @param emittente1                          Emittente dell'identificativo della persona.<p>Mappato in {@code Othr/Issr} di PersonIdentification13.</p>
 * @param identificativoSoggetto2             Secondo identificativo univoco della persona.<p>Mappato in {@code Othr/Id} di PersonIdentification13.</p>
 * @param codiceIdentificativoSoggetto2       Schema del secondo identificativo univoco della persona.<p>Mappato in {@code Othr/SchemeNm/Cd} di PersonIdentification13.</p>
 * @param codiceProprietarioIdentificativoSoggetto2 Proprietario del secondo schema identificativo.<p>Mappato in {@code Othr/SchemeNm/Prtry} di PersonIdentification13.</p>
 * @param emittente2                          Emittente del secondo identificativo della persona.<p>Mappato in {@code Othr/Issr} di PersonIdentification13.</p>
 *
 * @see InformazioniAttore
 * @see PersonIdentification13
 * @see Party38Choice
 * @see <a href="https://www.iso20022.org/"/>ISO 20022</a>
 */
@Table("privato")
public record Privato(

        /// Identificativo univoco del record.
        UUID id,

        /// Identificativo delle informazioni dell'attore.
        @Column("id_informazioni_attore")
        UUID idInformazioniAttore,

        /// Data di nascita della persona.
        /// <p>Mappato in `DtAndPlcOfBirth/BirthDt` di PersonIdentification13.</p>
        @Column("data_di_nascita")
        @DateKind(DateKind.DateKindEnum.DATE) 
        LocalDate dataDiNascita,

        /// Provincia di nascita della persona.
        /// <p>Mappato in `DtAndPlcOfBirth/PrvcOfBirth` di PersonIdentification13.</p>
        @Column("provincia_di_nascita")
        String provinciaDiNascita,

        /// Città di nascita della persona.
        /// <p>Mappato in `DtAndPlcOfBirth/CityOfBirth` di PersonIdentification13.</p>
        @Column("citta_di_nascita")
        String cittaDiNascita,

        /// Paese di nascita della persona.
        /// <p>Mappato in `DtAndPlcOfBirth/CtryOfBirth` di PersonIdentification13.</p>
        @Column("paese_di_nascita")
        String paeseDiNascita,

        /// Identificativo univoco della persona.
        /// <p>Mappato in `Othr/Id` di PersonIdentification13.</p>
        @Column("identificativo_soggetto_1")
        String identificativoSoggetto1,

        /// Schema dell'identificativo univoco della persona.
        /// <p>Mappato in `Othr/SchemeNm/Cd` di PersonIdentification13.</p>
        @Column("codice_identificativo_soggetto_1")
        String codiceIdentificativoSoggetto1,

        @Column("codice_proprietario_identificativo_soggetto_1")
        String codiceProprietarioIdentificativoSoggetto1,

        /// Emittente dell'identificativo della persona.
        /// <p>Mappato in `Othr/Issr` di PersonIdentification13.</p>
        @Column("emittente_1")
        String emittente1,

        /// Secondo identificativo univoco della persona.
        /// <p>Mappato in `Othr/Id` di PersonIdentification13.</p>
        @Column("identificativo_soggetto_2")
        String identificativoSoggetto2,

        /// Schema del secondo identificativo univoco della persona.
        /// <p>Mappato in `Othr/SchemeNm/Cd` di PersonIdentification13.</p>
        @Column("codice_identificativo_soggetto_2")
        String codiceIdentificativoSoggetto2,

        @Column("codice_proprietario_identificativo_soggetto_2")
        String codiceProprietarioIdentificativoSoggetto2,

        /// Emittente del secondo identificativo della persona.
        /// <p>Mappato in `Othr/Issr` di PersonIdentification13.</p>
        @Column("emittente_2")
        String emittente2
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<Privato, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<Privato> entityClass() {
            return Privato.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<Privato> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, Privato> decoder) {
            super(client, decoder);
        }

        public Uni<Privato> getByInformazioniAttore(UUID idInformazioniAttore) {
            return singleOrNull("SELECT * FROM privato WHERE id_informazioni_attore = $1", Tuple.of(idInformazioniAttore));
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
        public Privato getEntity() {
            return Privato.this;
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
                            this.informazioniAttore.privato = this;
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