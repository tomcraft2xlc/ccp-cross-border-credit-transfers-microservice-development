package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.InformazioniCausale;
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
 * Rappresenta i dettagli di una linea di un documento di riferimento,<p>
 * secondo lo schema ISO 20022 {@code RmtInf/Strd/RfrdDocInf/LineDtls}.<p>
 *
 * Questa classe memorizza le informazioni dettagliate relative a una singola voce
 * di un documento di riferimento, inclusi descrizione, importi, sconti, tasse e rettifiche.<p>
 *
 * La tabella associata nel database è {@code dettaglio_linea_documento_di_riferimento}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE dettaglio_linea_documento_di_riferimento (
 *     id UUID PRIMARY KEY,
 *     id_dettaglio_documento_di_riferimento UUID NOT NULL,
 *     descrizione_voce VARCHAR(255),
 *     CONSTRAINT fk_dettaglio_documento_di_riferimento FOREIGN KEY (id_dettaglio_documento_di_riferimento)
 *         REFERENCES dettaglio_documento_di_riferimento(id)
 * );
 * }</pre>
 *
 * @param id                                  Identificativo univoco del record.<p>
 * @param idInformazioniDocumentoDiRiferimento   Identificativo del dettaglio del documento di riferimento.<p>
 * @param descrizioneVoce                     Descrizione della voce nel documento di riferimento.<p>Mappato in {@code RmtInf/Strd/RfrdDocInf/LineDtls/Desc}.</p>
 */
@Table("dettaglio_linea_documento_di_riferimento")
public record DettaglioLineaDocumentoDiRiferimento(
        UUID id,

        @Column("id_dettaglio_documento_di_riferimento")
        String idInformazioniDocumentoDiRiferimento,

        @Column("descrizione_voce")
        String descrizioneVoce
) {

    public static final class Entity
            implements com.flowpay.ccp.persistence.Entity<DettaglioLineaDocumentoDiRiferimento, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<DettaglioLineaDocumentoDiRiferimento> entityClass() {
            return DettaglioLineaDocumentoDiRiferimento.class;
        }
    }

    public static final class Repository
            extends com.flowpay.ccp.persistence.Repository<DettaglioLineaDocumentoDiRiferimento> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, DettaglioLineaDocumentoDiRiferimento> decoder) {
            super(client, decoder);
        }

        public Uni<DettaglioLineaDocumentoDiRiferimento> getById(UUID id) {
            return single("SELECT * FROM dettaglio_linea_documento_di_riferimento WHERE id = $1", Tuple.of(id));
        }

        public Multi<DettaglioLineaDocumentoDiRiferimento> getAllByInformazioniDocumentoDiRiferimento(UUID idInformazioniDocumentoDiRiferimento) {
            return multi("SELECT * FROM dettaglio_linea_documento_di_riferimento WHERE id_dettaglio_documento_di_riferimento = $1", Tuple.of(idInformazioniDocumentoDiRiferimento));
        }
    }

    /**
     * Prepara a recuperare le entity linkate.
     */
    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {
        public InformazioniDocumentoDiRiferimento.WithLinkedEntities dettaglioDocumentoDiRiferimento;
        public Collection<DettaglioLineaDocumentoDiRiferimentoDettaglioImporto.WithLinkedEntities> dettaglioLineaDocumentoDiRiferimentoDettaglioImporti;
        public Collection<IdentificativoLineaDocumento.WithLinkedEntities> identificativiLinea;

        @Override
        public DettaglioLineaDocumentoDiRiferimento getEntity() {
            return DettaglioLineaDocumentoDiRiferimento.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(dettaglioDocumentoDiRiferimento);
            if (dettaglioLineaDocumentoDiRiferimentoDettaglioImporti != null) dettaglioLineaDocumentoDiRiferimentoDettaglioImporti.forEach(consumer);
            if (identificativiLinea != null) identificativiLinea.forEach(consumer);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.dettaglioDocumentoDiRiferimento == null) {
                multis.add(new InformazioniDocumentoDiRiferimento.Entity()
                        .repository(sqlClient).getById(idInformazioniDocumentoDiRiferimento())
                        .map(linked -> {
                            this.dettaglioDocumentoDiRiferimento = linked.withLinkedEntities();
                            return this.dettaglioDocumentoDiRiferimento;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.dettaglioDocumentoDiRiferimento));
            }

            if (this.dettaglioLineaDocumentoDiRiferimentoDettaglioImporti == null) {
                this.dettaglioLineaDocumentoDiRiferimentoDettaglioImporti = new ArrayList<>();
                multis.add(new DettaglioLineaDocumentoDiRiferimentoDettaglioImporto.Entity()
                        .repository(sqlClient).getAllByDettaglioLineaDocumentoDiRiferimento(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.dettaglioLineaDocumentoDiRiferimento = this;
                            this.dettaglioLineaDocumentoDiRiferimentoDettaglioImporti.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(
                        Multi.createFrom().iterable(this.dettaglioLineaDocumentoDiRiferimentoDettaglioImporti).onItem()
                                .castTo(EntityWithLinkedEntities.class));
            }

            if (this.identificativiLinea == null) {
                this.identificativiLinea = new ArrayList<>();
                multis.add(new IdentificativoLineaDocumento.Entity()
                        .repository(sqlClient).getAllByDettaglioLineaDocumentoDiRiferimento(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.dettaglioLineaDocumentoDiRiferimento = this;
                            this.identificativiLinea.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(
                        Multi.createFrom().iterable(this.identificativiLinea).onItem()
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