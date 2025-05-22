package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.InformazioniAttore;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.DettagliPignoramento;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.InformazioniCausale;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.DateKind;
import com.flowpay.ccp.persistence.Table;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Rappresenta le informazioni relative a un documento di riferimento in una transazione di pagamento,<p>
 * secondo lo schema ISO 20022 {@code RmtInf/Strd/RfrdDocInf}.<p>
 *
 * Questa classe memorizza i dettagli del documento di riferimento associato,
 * inclusi il tipo di documento, la descrizione, l'emittente, il numero identificativo
 * e la data di emissione.<p>
 *
 * La tabella associata nel database è {@code informazioni_documento_di_riferimento}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE informazioni_documento_di_riferimento (
 *     id UUID PRIMARY KEY,
 *     id_informazioni_causale UUID NOT NULL,
 *     tipo VARCHAR(255),
 *     descrizione VARCHAR(255),
 *     emittente VARCHAR(255),
 *     numero VARCHAR(255),
 *     data_documento_di_riferimento DATE,
 *     CONSTRAINT fk_informazioni_causale FOREIGN KEY (id_informazioni_causale)
 *         REFERENCES informazioni_causale(id)
 * );
 * }</pre>
 *
 * @param id                        Identificativo univoco del record.<p>
 * @param idInformazioniCausale      Identificativo delle informazioni di causale associate.<p>
 * @param tipo                       Tipo di documento di riferimento.<p>Mappato in {@code RmtInf/Strd/RfrdDocInf/Tp/CdOrPrtry/Cd}.</p>
 * @param descrizione                Descrizione del documento di riferimento.<p>Mappato in {@code RmtInf/Strd/RfrdDocInf/Tp/CdOrPrtry/Prtry}.</p>
 * @param emittente                  Emittente del documento di riferimento.<p>Mappato in {@code RmtInf/Strd/RfrdDocInf/Tp/Issr}.</p>
 * @param numero                     Numero identificativo del documento di riferimento.<p>Mappato in {@code RmtInf/Strd/RfrdDocInf/Nb}.</p>
 * @param data                       Data di emissione del documento di riferimento.<p>Mappato in {@code RmtInf/Strd/RfrdDocInf/RltdDt}.</p>
 *
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("informazioni_documento_di_riferimento")
public record InformazioniDocumentoDiRiferimento(
        UUID id,

        @Column("id_informazioni_causale")
        UUID idInformazioniCausale,

        /// Tipo di documento di riferimento.
        /// <p>Mappato in `RmtInf/Strd/RfrdDocInf/Tp/CdOrPrtry/Cd`.</p>
        String tipo,

        /// Descrizione del documento di riferimento.
        /// <p>Mappato in `RmtInf/Strd/RfrdDocInf/Tp/CdOrPrtry/Prtry`.</p>
        String descrizione,

        /// Emittente del documento di riferimento.
        /// <p>Mappato in `RmtInf/Strd/RfrdDocInf/Tp/Issr`.</p>
        String emittente,

        /// Numero identificativo del documento di riferimento.
        /// <p>Mappato in `RmtInf/Strd/RfrdDocInf/Nb`.</p>
        String numero,

        /// Data di emissione del documento di riferimento.
        /// <p>Mappato in `RmtInf/Strd/RfrdDocInf/RltdDt`.</p>
        @Column("data_documento_di_riferimento")
        @DateKind(DateKind.DateKindEnum.DATE)
        LocalDate data
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<InformazioniDocumentoDiRiferimento, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<InformazioniDocumentoDiRiferimento> entityClass() {
            return InformazioniDocumentoDiRiferimento.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<InformazioniDocumentoDiRiferimento> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, InformazioniDocumentoDiRiferimento> decoder) {
            super(client, decoder);
        }

        public Multi<InformazioniDocumentoDiRiferimento> getAllByInformazioniCausale(UUID idInformazioniCausale) {
            return multi("SELECT * FROM informazioni_documento_di_riferimento WHERE id_informazioni_causale = $1",
                    Tuple.of(idInformazioniCausale));
        }

        public Uni<InformazioniDocumentoDiRiferimento> getById(String id) {
            return single("SELECT * FROM informazioni_documento_di_riferimento WHERE id = $1", Tuple.of(id));
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
        public Collection<DettaglioLineaDocumentoDiRiferimento.WithLinkedEntities> dettagliLineeDocumentoDiRiferimento;

        @Override
        public InformazioniDocumentoDiRiferimento getEntity() {
            return InformazioniDocumentoDiRiferimento.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(informazioniCausale);
            if (dettagliLineeDocumentoDiRiferimento != null) dettagliLineeDocumentoDiRiferimento.forEach(consumer);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.informazioniCausale == null) {
                multis.add(new InformazioniCausale.Entity()
                        .repository(sqlClient).getById(idInformazioniCausale())
                        .map(linked -> {
                            this.informazioniCausale = linked.withLinkedEntities();
                            // Adding this to the parent would not be so beneficial as 
                            // the sibling would need to be loaded, and loading is 
                            // done en-masse.
                            return this.informazioniCausale;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.informazioniCausale));
            }

            if (this.dettagliLineeDocumentoDiRiferimento == null) {
                this.dettagliLineeDocumentoDiRiferimento = new ArrayList<>();
                multis.add(new DettaglioLineaDocumentoDiRiferimento.Entity()
                        .repository(sqlClient).getAllByInformazioniDocumentoDiRiferimento(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.dettaglioDocumentoDiRiferimento = this;
                            this.dettagliLineeDocumentoDiRiferimento.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.dettagliLineeDocumentoDiRiferimento).onItem()
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