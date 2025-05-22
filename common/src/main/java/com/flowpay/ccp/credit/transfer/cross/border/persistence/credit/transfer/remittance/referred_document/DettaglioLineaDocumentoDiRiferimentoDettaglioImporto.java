package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
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
 * Rappresenta l'associazione tra una linea di dettaglio di un documento di riferimento
 * e un dettaglio di importo.<p>
 *
 * Questa classe serve per collegare un elemento di una linea di dettaglio
 * di un documento di riferimento con un valore di importo specifico.<p>
 *
 * La tabella associata nel database è {@code dettaglio_linea_documento_di_riferimento_dettaglio_importo}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE dettaglio_linea_documento_di_riferimento_dettaglio_importo (
 *     id UUID PRIMARY KEY,
 *     id_dettaglio_linea UUID NOT NULL,
 *     id_dettaglio_importo UUID NOT NULL,
 *     CONSTRAINT fk_dettaglio_linea FOREIGN KEY (id_dettaglio_linea)
 *         REFERENCES dettaglio_linea_documento_di_riferimento(id),
 *     CONSTRAINT fk_dettaglio_importo FOREIGN KEY (id_dettaglio_importo)
 *         REFERENCES dettaglio_importo(id)
 * );
 * }</pre>
 *
 * @param id                 Identificativo univoco del record.<p>
 * @param idDettaglioLinea    Identificativo della linea di dettaglio associata.<p>
 * @param idDettaglioImporto  Identificativo del dettaglio di importo associato.<p>
 *
 * @see DettaglioLineaDocumentoDiRiferimento
 * @see DettaglioImporto
 */
@Table("dettaglio_linea_documento_di_riferimento_dettaglio_importo")
public record DettaglioLineaDocumentoDiRiferimentoDettaglioImporto(
        UUID id,

        /// Identificativo della linea di dettaglio associata.
        @Column("id_dettaglio_linea")
        UUID idDettaglioLinea,

        /// Identificativo del dettaglio di importo associato.
        @Column("id_dettaglio_importo")
        UUID idDettaglioImporto
) {

    public static final class Entity
            implements com.flowpay.ccp.persistence.Entity<DettaglioLineaDocumentoDiRiferimentoDettaglioImporto, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<DettaglioLineaDocumentoDiRiferimentoDettaglioImporto> entityClass() {
            return DettaglioLineaDocumentoDiRiferimentoDettaglioImporto.class;
        }
    }

    public static final class Repository
            extends com.flowpay.ccp.persistence.Repository<DettaglioLineaDocumentoDiRiferimentoDettaglioImporto> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, DettaglioLineaDocumentoDiRiferimentoDettaglioImporto> decoder) {
            super(client, decoder);
        }

        public Uni<DettaglioLineaDocumentoDiRiferimentoDettaglioImporto> getById(UUID id) {
            return single("SELECT * FROM dettaglio_linea_documento_di_riferimento_dettaglio_importo WHERE id = $1", Tuple.of(id));
        }

        public Multi<DettaglioLineaDocumentoDiRiferimentoDettaglioImporto> getAllByDettaglioLineaDocumentoDiRiferimento(UUID idDettaglioLineaDocumentoDiRiferimento) {
            return multi("SELECT * FROM dettaglio_linea_documento_di_riferimento_dettaglio_importo WHERE id_dettaglio_linea = $1", Tuple.of(idDettaglioLineaDocumentoDiRiferimento));
        }

    }

    /**
     * Prepara a recuperare le entity linkate.
     */
    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {
        public DettaglioLineaDocumentoDiRiferimento.WithLinkedEntities dettaglioLineaDocumentoDiRiferimento;
        public DettaglioImporto.WithLinkedEntities dettaglioImporto;

        @Override
        public DettaglioLineaDocumentoDiRiferimentoDettaglioImporto getEntity() {
            return DettaglioLineaDocumentoDiRiferimentoDettaglioImporto.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(dettaglioLineaDocumentoDiRiferimento);
            consumer.accept(dettaglioImporto);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.dettaglioLineaDocumentoDiRiferimento == null) {
                multis.add(new DettaglioLineaDocumentoDiRiferimento.Entity()
                        .repository(sqlClient).getById(idDettaglioLinea())
                        .map(linked -> {
                            this.dettaglioLineaDocumentoDiRiferimento = linked.withLinkedEntities();
                            return this.dettaglioLineaDocumentoDiRiferimento;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.dettaglioLineaDocumentoDiRiferimento));
            }

            if (this.dettaglioImporto == null) {
                multis.add(new DettaglioImporto.Entity()
                        .repository(sqlClient).getById(idDettaglioImporto())
                        .map(linked -> {
                            this.dettaglioImporto = linked.withLinkedEntities();
                            return this.dettaglioImporto;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {    
                multis.add(Multi.createFrom().item(this.dettaglioImporto));
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