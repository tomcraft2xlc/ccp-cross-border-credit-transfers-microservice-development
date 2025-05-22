package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account_to_account;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.Table;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

/**
 * Rappresenta le informazioni relative ad un NDG.
 * 
 * SQL per la creazione della tabella:
 * <pre>{@code
 * CREATE TABLE informazioni_ndg (
 *     id UUID PRIMARY KEY,
 *     id_dettaglio_bonifico UUID NOT NULL,
 *     tipo tipo_ndg NOT NULL,
 *     ndg VARCHAR(255) NOT NULL,
 *     nome VARCHAR(255) NOT NULL
 * 
 *     CONSTRAINT fk_dettaglio_bonifico FOREIGN KEY (id_dettaglio_bonifico)
 *         REFERENCES dettaglio_bonifico(id)
 * );
 * }</pre>
 * 
 * @param id Identificativo univoco del record ndg
 * @param idDettaglioBonifico Dettaglio bonifico a cui questo record é collegato
 * @param tipo Tipo del record Ndg
 * @param ndg Codice NDG
 * @param nome Nome del soggetto
 */
@Table("informazioni_ndg")
public record InformazioniNdg(
        /// Identificativo univoco del record ndg
        UUID id,
        /// Dettaglio bonifico a cui questo record è collegato
        @Column("id_dettaglio_bonifico")
        UUID idDettaglioBonifico,
        /// Tipo del record Ndg
        TipoNdg tipo,

        String ndg,
        String nome,
        @Column("codice_fiscale")
        String codiceFiscale) {


    public static final class Entity implements com.flowpay.ccp.persistence.Entity<InformazioniNdg, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<InformazioniNdg> entityClass() {
            return InformazioniNdg.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<InformazioniNdg> {

        public Repository(SqlClient client, java.util.function.Function<io.vertx.mutiny.sqlclient.Row, InformazioniNdg> decoder) {
            super(client, decoder);
        }

        public Multi<InformazioniNdg> getAllByDettaglioBonifico(UUID idDettaglioBonifico) {
            return multi("SELECT * FROM informazioni_ndg WHERE id_dettaglio_bonifico = $1", Tuple.of(idDettaglioBonifico));
        }

    }


    /**
     * Prepara a recuperare le entity linkate.
     */
    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {
        public DettaglioBonificoAccountToAccount.WithLinkedEntities dettaglioBonificoAccountToAccount;

        @Override
        public InformazioniNdg getEntity() {
            return InformazioniNdg.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(dettaglioBonificoAccountToAccount);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.dettaglioBonificoAccountToAccount == null) {
                multis.add(new DettaglioBonificoAccountToAccount.Entity()
                        .repository(sqlClient).getById(idDettaglioBonifico())
                        .map(linked -> {
                            this.dettaglioBonificoAccountToAccount = linked.withLinkedEntities();
                            // Do not fill parent property
                            return this.dettaglioBonificoAccountToAccount;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.dettaglioBonificoAccountToAccount));
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
