package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.Table;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Rappresenta una commissione associata a un bonifico account-to-account nel sistema di pagamento.<p>
 *
 * Questa classe mappa la tabella {@code commissione_account_to_account} nel database e contiene
 * informazioni relative al tipo di regolamento, alla descrizione della commissione e agli importi applicati.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE commissione_account_to_account (
 *     id UUID PRIMARY KEY,
 *     id_bonifico_extra_sepa UUID NOT NULL,
 *     regolamento_commissione regolamento_commissione NOT NULL,
 *     descrizione VARCHAR(255),
 *     importo NUMERIC(18,2),
 *     percentuale NUMERIC(5,2),
 *     max NUMERIC(18,2),
 *     min NUMERIC(18,2),
 *     CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
 *         REFERENCES bonifico_extra_sepa(id)
 * );
 * }</pre>
 *
 * @param id                        Identificativo univoco della commissione<p>
 * @param idBonificoExtraSepa       Identificativo del bonifico extra-SEPA associato<p>
 * @param descrizione               Descrizione della commissione<p>
 * @param importo                   Importo della commissione in caso di commissione fissa<p>
 * @param percentuale               Percentuale della commissione in caso di commissione percentuale<p>
 * @param max                       Importo massimo della commissione in caso di commissione percentuale<p>
 * @param min                        Importo minimo della commissione in caso di commissione percentuale<p>
 */
@Table("commissione_account_to_account")
public record CommissioneAccountToAccount(

        /// Identificativo univoco della commissione
        UUID id,

        /// Identificativo del bonifico extra-SEPA associato
        @Column("id_bonifico_extra_sepa")
        UUID idBonificoExtraSepa,

        String codice,

        /// Descrizione della commissione
        String descrizione,

        /// Importo della commissione in caso di commissione fissa
        BigDecimal importo,

        /// Percentuale della commissione
        BigDecimal percentuale,

        /// Importo massimo della commissione in caso di commissione percentuale
        BigDecimal max,

        /// Importo minimo della commissione in caso di commissione percentuale
        BigDecimal min,

        String divisa
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<CommissioneAccountToAccount, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<CommissioneAccountToAccount> entityClass() {
            return CommissioneAccountToAccount.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<CommissioneAccountToAccount> {

        public Repository(SqlClient client, java.util.function.Function<io.vertx.mutiny.sqlclient.Row, CommissioneAccountToAccount> decoder) {
            super(client, decoder);
        }

        public Multi<CommissioneAccountToAccount> getAllByBonificoExtraSepa(UUID idBonificoExtraSepa) {
            return multi("SELECT * FROM commissione_account_to_account WHERE id_bonifico_extra_sepa = $1", Tuple.of(idBonificoExtraSepa));
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

        @Override
        public CommissioneAccountToAccount getEntity() {
            return CommissioneAccountToAccount.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }
        
        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(bonificoExtraSepa);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.bonificoExtraSepa == null) {
                multis.add(new BonificoExtraSepa.Entity()
                        .repository(sqlClient).getById(idBonificoExtraSepa())
                        .map(linked -> {
                            this.bonificoExtraSepa = linked.withLinkedEntities();
                            // Do not add to the parent
                            return this.bonificoExtraSepa;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.bonificoExtraSepa));
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