package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.InformazioniAttore;
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
 * Rappresenta i dettagli relativi a un pignoramento,<p>
 * secondo lo schema ISO 20022 {@code RmtInf/Strd/GrnshmtRmt}.<p>
 *
 * Questa classe memorizza informazioni sul pignoramento,
 * inclusi il codice, il codice proprietario, l'emittente e le entità coinvolte.<p>
 *
 * La tabella associata nel database è {@code dettagli_pignoramento}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE dettagli_pignoramento (
 *     id UUID PRIMARY KEY,
 *     id_informazioni_causale UUID NOT NULL,
 *     codice VARCHAR(255),
 *     codice_proprietario VARCHAR(255),
 *     emittente VARCHAR(255),
 *     id_terzo_pignorato UUID,
 *     id_gestore_pignoramento UUID,
 *     identificativo_pignoramento VARCHAR(255),
 *     data_pignoramento DATE,
 *     importo_pignoramento NUMERIC(18,2),
 *     divisa_importo_pignoramento VARCHAR(3),
 *     assicurazione_sanitaria BOOLEAN,
 *     disoccupato BOOLEAN,
 *     CONSTRAINT fk_informazioni_causale FOREIGN KEY (id_informazioni_causale)
 *         REFERENCES informazioni_causale(id),
 *     CONSTRAINT fk_terzo_pignorato FOREIGN KEY (id_terzo_pignorato)
 *         REFERENCES terzo_pignorato(id),
 *     CONSTRAINT fk_gestore_pignoramento FOREIGN KEY (id_gestore_pignoramento)
 *         REFERENCES gestore_pignoramento(id)
 * );
 * }</pre>
 *
 * @param id                        Identificativo univoco del record.<p>
 * @param idInformazioniCausale      Identificativo delle informazioni di causale associate.<p>
 * @param codice                     Codice di riferimento del pignoramento.<p>Mappato in {@code RmtInf/Strd/GrnshmtRmt/Tp/CdOrPrtry/Cd}.</p>
 * @param codiceProprietario         Codice proprietario del pignoramento.<p>Mappato in {@code RmtInf/Strd/GrnshmtRmt/Tp/CdOrPrtry/Prtry}.</p>
 * @param emittente                  Emittente del pignoramento.<p>Mappato in {@code RmtInf/Strd/GrnshmtRmt/Tp/Issr}.</p>
 * @param idTerzoPignorato           Identificativo del terzo pignorato.<p>Mappato in {@code RmtInf/Strd/GrnshmtRmt/Grnshee}.</p>
 * @param idGestorePignoramento      Identificativo del gestore del pignoramento.<p>Mappato in {@code RmtInf/Strd/GrnshmtRmt/GrnshmtAdmstr}.</p>
 * @param identificativoPignoramento Identificativo specifico del pignoramento.<p>Mappato in {@code RmtInf/Strd/GrnshmtRmt/RefNb}.</p>
 * @param dataPignoramento           Data del pignoramento.<p>Mappato in {@code RmtInf/Strd/GrnshmtRmt/Dt}.</p>
 * @param importoPignoramento        Importo associato al pignoramento.<p>Mappato in {@code RmtInf/Strd/GrnshmtRmt/RmtdAmt}.</p>
 * @param divisaImportoPignoramento  Divisa dell'importo pignorato.<p>Mappato in {@code RmtInf/Strd/GrnshmtRmt/RmtdAmt/Ccy}.</p>
 * @param assicurazioneSanitaria     Indica se il pignoramento è relativo all'assicurazione sanitaria.<p>Mappato in {@code RmtInf/Strd/Grnshmt/FmlyMdclInsrncInd}.</p>
 * @param disoccupato                Indica se il soggetto è disoccupato.<p>Mappato in {@code RmtInf/Strd/Grnshmt/MplyeeTermntnInd}.</p>
 *
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("dettagli_pignoramento")
public record DettagliPignoramento(
        UUID id,

        @Column("id_informazioni_causale")
        UUID idInformazioniCausale,

        /// Codice di riferimento del pignoramento.
        /// <p>Mappato in `RmtInf/Strd/GrnshmtRmt/Tp/CdOrPrtry/Cd`.</p>
        String codice,

        /// Codice proprietario del pignoramento.
        /// <p>Mappato in `RmtInf/Strd/GrnshmtRmt/Tp/CdOrPrtry/Prtry`.</p>
        @Column("codice_proprietario")
        String codiceProprietario,

        /// Emittente del pignoramento.
        /// <p>Mappato in `RmtInf/Strd/GrnshmtRmt/Tp/Issr`.</p>
        String emittente,

        /// Identificativo del terzo pignorato.
        /// <p>Mappato in `RmtInf/Strd/GrnshmtRmt/Grnshee`.</p>
        @Column("id_terzo_pignorato")
        UUID idTerzoPignorato,

        /// Identificativo del gestore del pignoramento.
        /// <p>Mappato in `RmtInf/Strd/GrnshmtRmt/GrnshmtAdmstr`.</p>
        @Column("id_gestore_pignoramento")
        UUID idGestorePignoramento,

        /// RmtInf/Strd/GrnshmtRmt/RefNb
        @Column("identificativo_pignoramento")
        String identificativoPignoramento,

        /// RmtInf/Strd/GrnshmtRmt/Dt
        @Column("data_pignoramento")
        @DateKind(DateKind.DateKindEnum.DATE)
        LocalDate dataPignoramento,

        /// RmtInf/Strd/GrnshmtRmt/RmtdAmt
        @Column("importo_pignoramento")
        BigDecimal importoPignoramento,

        /// RmtInf/Strd/GrnshmtRmt/RmtdAmt/Ccy
        @Column("divisa_importo_pignoramento")
        String divisaImportoPignoramento,

        /// RmtInf/Strd/Grnshmt/FmlyMdclInsrncInd
        @Column("assicurazione_sanitaria")
        Boolean assicurazioneSanitaria,

        /// RmtInf/Strd/Grnshmt/MplyeeTermntnInd
        Boolean disoccupato
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<DettagliPignoramento, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<DettagliPignoramento> entityClass() {
            return DettagliPignoramento.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<DettagliPignoramento> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, DettagliPignoramento> decoder) {
            super(client, decoder);
        }

        public Uni<DettagliPignoramento> getByInformazioniCausale(UUID idInformazioniCausale) {
            return singleOrNull("SELECT * FROM dettagli_pignoramento WHERE id_informazioni_causale = $1",
                    Tuple.of(idInformazioniCausale));
        }
    }

    /**
     * Prepara a recuperare le entity linkate.
     */
    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {
        public InformazioniCausale.WithLinkedEntities informazioniCausale;
        public InformazioniAttore.WithLinkedEntities terzoPignorato;
        public InformazioniAttore.WithLinkedEntities gestorePignoramento;

        @Override
        public DettagliPignoramento getEntity() {
            return DettagliPignoramento.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(informazioniCausale);
            consumer.accept(terzoPignorato);
            consumer.accept(gestorePignoramento);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.informazioniCausale == null) {
                multis.add(new InformazioniCausale.Entity()
                        .repository(sqlClient).getById(idInformazioniCausale())
                        .map(linked -> {
                            this.informazioniCausale = linked.withLinkedEntities();
                            this.informazioniCausale.dettagliPignoramento = this;
                            return this.informazioniCausale;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.informazioniCausale));
            }

            if (this.terzoPignorato == null) {
                multis.add(new InformazioniAttore.Entity()
                        .repository(sqlClient).getById(idTerzoPignorato())
                        .map(linked -> {
                            this.terzoPignorato = linked.withLinkedEntities();
                            return this.terzoPignorato;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.terzoPignorato));
            }

            if (this.gestorePignoramento == null) {
                multis.add(new InformazioniAttore.Entity()
                        .repository(sqlClient).getById(idGestorePignoramento())
                        .map(linked -> {
                            this.gestorePignoramento = linked.withLinkedEntities();
                            return this.gestorePignoramento;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.gestorePignoramento));
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