package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.InformazioniAttore;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document.InformazioniCausaleDettaglioImporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document.InformazioniDocumentoDiRiferimento;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax.DettagliFiscali;
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
 * Rappresenta le informazioni relative alla causale di un pagamento,
 * <p>
 * secondo lo schema ISO 20022 {@code RmtInf}.
 * <p>
 *
 * Questa classe memorizza i dettagli della causale descrittiva,
 * il riferimento univoco del creditore e le informazioni sugli attori coinvolti
 * nel documento.
 * <p>
 *
 * La tabella associata nel database è {@code informazioni_causale}.
 * <p>
 *
 * SQL per la creazione della tabella:
 * <p>
 * 
 * <pre>{@code
 * CREATE TABLE informazioni_causale (
 *     id UUID PRIMARY KEY,
 *     id_bonifico_extra_sepa UUID NOT NULL,
 *     causale_descrittiva VARCHAR(255),
 *     tipo_riferimento_creditore VARCHAR(255),
 *     descrizione_riferimento_creditore VARCHAR(255),
 *     emittente_riferimento_creditore VARCHAR(255),
 *     riferimento_univoco_creditore VARCHAR(255),
 *     id_attore_emittente_documento UUID,
 *     id_attore_ricevente_documento UUID,
 *     ulteriori_informazioni TEXT,
 *     CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
 *         REFERENCES bonifico_extra_sepa(id),
 *     CONSTRAINT fk_attore_emittente_documento FOREIGN KEY (id_attore_emittente_documento)
 *         REFERENCES informazioni_attore(id),
 *     CONSTRAINT fk_attore_ricevente_documento FOREIGN KEY (id_attore_ricevente_documento)
 *         REFERENCES informazioni_attore(id)
 * );
 * }</pre>
 *
 * @param id                              Identificativo univoco del record.
 *                                        <p>
 * @param idBonificoExtraSepa             Identificativo del bonifico extra SEPA
 *                                        associato.
 *                                        <p>
 * @param causaleDescrittiva              Causale descrittiva del pagamento.
 *                                        <p>
 *                                        Mappato in {@code RmtInf/Ustrd}.
 *                                        </p>
 * @param tipoRiferimentoCreditore        Tipo di riferimento del creditore.
 *                                        <p>
 *                                        Mappato in
 *                                        {@code RmtInf/Strd/CdtrRefInf/Tp/CdOrPrtry/Cd}.
 *                                        </p>
 * @param descrizioneRiferimentoCreditore Descrizione del riferimento del
 *                                        creditore.
 *                                        <p>
 *                                        Mappato in
 *                                        {@code RmtInf/Strd/CdtrRefInf/Tp/CdOrPrtry/Prtry}.
 *                                        </p>
 * @param emittenteRiferimentoCreditore   Emittente del riferimento del
 *                                        creditore.
 *                                        <p>
 *                                        Mappato in
 *                                        {@code RmtInf/Strd/CdtrRefInf/Issr}.
 *                                        </p>
 * @param riferimentoUnivocoCreditore     Riferimento univoco del creditore.
 *                                        <p>
 * @param idAttoreEmittenteDocumento      Identificativo dell'attore emittente
 *                                        del documento.
 *                                        <p>
 *                                        Mappato in {@code RmtInf/Strd/Invcr}.
 *                                        </p>
 * @param idAttoreRiceventeDocumento      Identificativo dell'attore ricevente
 *                                        del documento.
 *                                        <p>
 *                                        Mappato in {@code RmtInf/Strd/Invcee}.
 *                                        </p>
 * @param ulterioriInformazioni           Ulteriori informazioni sulla causale.
 *                                        <p>
 *                                        Mappato in
 *                                        {@code RmtInf/Strd/AddtlRmtInf}.
 *                                        </p>
 *
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("informazioni_causale")
public record InformazioniCausale(
        UUID id,

        @Column("causale_documento_collegato")
        Boolean causaleDocumentoCollegato,

        @Column("id_bonifico_extra_sepa") UUID idBonificoExtraSepa,

        /// Causale descrittiva del pagamento.
        /// <p>Mappato in `RmtInf/Ustrd`.</p>
        @Column("causale_descrittiva") String causaleDescrittiva,

        /// Tipo di riferimento del creditore.
        /// <p>Mappato in `RmtInf/Strd/CdtrRefInf/Tp/CdOrPrtry/Cd`.</p>
        @Column("tipo_riferimento_creditore") String tipoRiferimentoCreditore,

        /// Descrizione del riferimento del creditore.
        /// <p>Mappato in `RmtInf/Strd/CdtrRefInf/Tp/CdOrPrtry/Prtry`.</p>
        @Column("descrizione_riferimento_creditore") String descrizioneRiferimentoCreditore,

        /// Emittente del riferimento del creditore.
        /// <p>Mappato in `RmtInf/Strd/CdtrRefInf/Issr`.</p>
        @Column("emittente_riferimento_creditore") String emittenteRiferimentoCreditore,

        /// Riferimento univoco del creditore.
        @Column("riferimento_univoco_creditore") String riferimentoUnivocoCreditore,

        /// Identificativo dell'attore emittente del documento.
        /// <p>Mappato in `RmtInf/Strd/Invcr`.</p>
        @Column("id_attore_emittente_documento") UUID idAttoreEmittenteDocumento,

        /// Identificativo dell'attore ricevente del documento.
        /// <p>Mappato in `RmtInf/Strd/Invcee`.</p>
        @Column("id_attore_ricevente_documento") UUID idAttoreRiceventeDocumento,

        /// Ulteriori informazioni sulla causale.
        /// <p>Mappato in `RmtInf/Strd/AddtlRmtInf`.</p>
        @Column("ulteriori_informazioni") String ulterioriInformazioni) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<InformazioniCausale, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<InformazioniCausale> entityClass() {
            return InformazioniCausale.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<InformazioniCausale> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, InformazioniCausale> decoder) {
            super(client, decoder);
        }

        public Multi<InformazioniCausale> getAllByBonificoExtraSepa(UUID idBonificoExtraSepa, Boolean documentoCollegato) {
            return multi("""
                    SELECT *
                    FROM informazioni_causale
                    WHERE
                        id_bonifico_extra_sepa = $1 AND
                        causale_documento_collegato = $2
                    """,
                    Tuple.of(idBonificoExtraSepa, documentoCollegato));
        }

        public Uni<InformazioniCausale> getById(UUID id) {
            return single("SELECT * FROM informazioni_causale WHERE id = $1",
                    Tuple.of(id));
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
        public InformazioniAttore.WithLinkedEntities attoreEmittenteDocumento;
        public InformazioniAttore.WithLinkedEntities attoreRiceventeDocumento;
        public DettagliFiscali.WithLinkedEntities dettagliFiscali;
        public DettagliPignoramento.WithLinkedEntities dettagliPignoramento;
        public Collection<InformazioniDocumentoDiRiferimento.WithLinkedEntities> informazioniDocumentiDiRiferimento;
        public Collection<InformazioniCausaleDettaglioImporto.WithLinkedEntities> informazioniCausaleDettaglioImporti;

        @Override
        public InformazioniCausale getEntity() {
            return InformazioniCausale.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(bonificoExtraSepa);
            consumer.accept(attoreEmittenteDocumento);
            consumer.accept(attoreRiceventeDocumento);
            consumer.accept(dettagliFiscali);
            consumer.accept(dettagliPignoramento);
            if (informazioniCausaleDettaglioImporti != null)
                informazioniCausaleDettaglioImporti.forEach(consumer);
            if (informazioniDocumentiDiRiferimento != null)
                informazioniDocumentiDiRiferimento.forEach(consumer);
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

            if (this.attoreEmittenteDocumento == null) {
                if (idAttoreEmittenteDocumento() != null) {
                    multis.add(new InformazioniAttore.Entity()
                            .repository(sqlClient).getById(idAttoreEmittenteDocumento())
                            .map(linked -> {
                                this.attoreEmittenteDocumento = linked.withLinkedEntities();
                                this.attoreEmittenteDocumento.bonificoExtraSepa = this.bonificoExtraSepa;
                                return this.attoreEmittenteDocumento;
                            })
                            .onItem().castTo(EntityWithLinkedEntities.class)
                            .toMulti());
                }
            } else {
                multis.add(Multi.createFrom().item(this.attoreEmittenteDocumento));
            }

            if (this.attoreRiceventeDocumento == null) {
                if (idAttoreRiceventeDocumento() != null) {
                    multis.add(new InformazioniAttore.Entity()
                            .repository(sqlClient).getById(idAttoreRiceventeDocumento())
                            .map(linked -> {
                                this.attoreRiceventeDocumento = linked.withLinkedEntities();
                                this.attoreRiceventeDocumento.bonificoExtraSepa = this.bonificoExtraSepa;
                                return this.attoreRiceventeDocumento;
                            })
                            .onItem().castTo(EntityWithLinkedEntities.class)
                            .toMulti());
                }
            } else {
                multis.add(Multi.createFrom().item(this.attoreRiceventeDocumento));
            }

            if (this.dettagliFiscali == null) {
                multis.add(new DettagliFiscali.Entity()
                        .repository(sqlClient).getByInformazioniCausale(id())
                        .map(linked -> {
                            if (linked == null) {
                                return null;
                            }
                            this.dettagliFiscali = linked.withLinkedEntities();
                            this.dettagliFiscali.informazioniCausale = this;
                            return this.dettagliFiscali;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.dettagliFiscali));
            }

            if (this.dettagliPignoramento == null) {
                multis.add(new DettagliPignoramento.Entity()
                        .repository(sqlClient).getByInformazioniCausale(id())
                        .map(linked -> {
                            if (linked == null) {
                                return null;
                            }
                            this.dettagliPignoramento = linked.withLinkedEntities();
                            this.dettagliPignoramento.informazioniCausale = this;
                            return this.dettagliPignoramento;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.dettagliPignoramento));
            }

            if (this.informazioniDocumentiDiRiferimento == null) {
                this.informazioniDocumentiDiRiferimento = new ArrayList<>();
                multis.add(new InformazioniDocumentoDiRiferimento.Entity()
                        .repository(sqlClient).getAllByInformazioniCausale(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.informazioniCausale = this;
                            this.informazioniDocumentiDiRiferimento.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.informazioniDocumentiDiRiferimento).onItem()
                        .castTo(EntityWithLinkedEntities.class));
            }

            if (this.informazioniCausaleDettaglioImporti == null) {
                this.informazioniCausaleDettaglioImporti = new ArrayList<>();
                multis.add(new InformazioniCausaleDettaglioImporto.Entity()
                        .repository(sqlClient).getAllByInformazioniCausale(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.informazioniCausale = this;
                            this.informazioniCausaleDettaglioImporti.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.informazioniCausaleDettaglioImporti).onItem()
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