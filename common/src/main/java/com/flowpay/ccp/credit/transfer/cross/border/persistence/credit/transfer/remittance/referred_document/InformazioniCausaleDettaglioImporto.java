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
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Rappresenta l'associazione tra una causale e un dettaglio di importo.<p>
 *
 * Questa classe serve per collegare una causale con un valore di importo specifico.<p>
 *
 * La tabella associata nel database è {@code informazioni_causale_dettaglio_importo}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE informazioni_causale_dettaglio_importo (
 *     id UUID PRIMARY KEY,
 *     id_informazioni_causale UUID NOT NULL,
 *     id_dettaglio_importo UUID NOT NULL,
 *     CONSTRAINT fk_informazioni_causale FOREIGN KEY (id_informazioni_causale)
 *         REFERENCES informazioni_causale(id),
 *     CONSTRAINT fk_dettaglio_importo FOREIGN KEY (id_dettaglio_importo)
 *         REFERENCES dettaglio_importo(id)
 * );
 * }</pre>
 *
 * @param id                    Identificativo univoco del record.<p>
 * @param idInformazioniCausale Identificativo della causale associata.<p>
 * @param idDettaglioImporto    Identificativo del dettaglio di importo associato.<p>
 *
 * @see InformazioniCausale
 * @see DettaglioImporto
 */
@Table("informazioni_causale_dettaglio_importo")
public record InformazioniCausaleDettaglioImporto(
        UUID id,

        @Column("id_informazioni_causale")
        UUID idInformazioniCausale,

        @Column("id_dettaglio_importo")
        UUID idDettaglioImporto
) {
    public static final class Entity
            implements com.flowpay.ccp.persistence.Entity<InformazioniCausaleDettaglioImporto, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<InformazioniCausaleDettaglioImporto> entityClass() {
            return InformazioniCausaleDettaglioImporto.class;
        }
    }

    public static final class Repository
            extends com.flowpay.ccp.persistence.Repository<InformazioniCausaleDettaglioImporto> {

        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, InformazioniCausaleDettaglioImporto> decoder) {
            super(client, decoder);
        }

        public Multi<InformazioniCausaleDettaglioImporto> getAllByInformazioniCausale(UUID idInformazioniCausale) {
            return multi("SELECT * FROM informazioni_causale_dettaglio_importo WHERE id_informazioni_causale = $1",
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
        public DettaglioImporto.WithLinkedEntities dettaglioImporto;

        @Override
        public InformazioniCausaleDettaglioImporto getEntity() {
            return InformazioniCausaleDettaglioImporto.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(informazioniCausale);
            consumer.accept(dettaglioImporto);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.informazioniCausale == null) {
                multis.add(new InformazioniCausale.Entity()
                        .repository(sqlClient).getById(idInformazioniCausale())
                        .map(linked -> {
                            this.informazioniCausale = linked.withLinkedEntities();
                            return this.informazioniCausale;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());    

            } else {
                multis.add(Multi.createFrom().item(this.informazioniCausale));
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
