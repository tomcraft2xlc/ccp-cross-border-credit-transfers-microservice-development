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
 * Rappresenta una commissione bancaria associata a un bonifico nel sistema di pagamento.<p>
 *
 * Questa classe mappa la tabella {@code commissione_banca_a_banca} nel database e contiene
 * informazioni relative al tipo di regolamento, alla descrizione della commissione e agli importi applicati.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE commissione_banca_a_banca (
 *     id UUID PRIMARY KEY,
 *     id_bonifico_extra_sepa UUID NOT NULL,
 *     tipologia tipologia_commissioni NOT NULL,
 *     regolamento regolamento_commissione NOT NULL,
 *     descrizione VARCHAR(255),
 *     divisa VARCHAR(3),
 *     importo NUMERIC(18,2) NOT NULL,
 *     CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
 *         REFERENCES bonifico_extra_sepa(id)
 * );
 * }</pre>
 *
 * @param id                        Identificativo univoco della commissione<p>
 * @param idBonificoExtraSepa       Identificativo del bonifico extra-SEPA associato<p>
 * @param regolamento               Tipologia di regolamento della commissione<p>
 * @param descrizione               Descrizione della commissione<p>
 * @param divisa                    Divisa della commissione<p>
 * @param importo                   Importo della commissione ({@code ChrgsInf/Amt})<p>
 */
@Table("commissione_banca_a_banca")
public record CommissioneBanca(

        /// Identificativo univoco della commissione
        UUID id,

        /// Identificativo del bonifico extra-SEPA associato
        @Column("id_bonifico_extra_sepa")
        UUID idBonificoExtraSepa,

        String codice,

        /// Descrizione della commissione
        @Column("descrizione")
        String descrizione,

        /// Divisa della commissione
        @Column("divisa")
        String divisa,

        /// Importo della commissione (ISO 20022: {@code ChrgsInf/Amt})
        @Column("importo")
        BigDecimal importo
) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<CommissioneBanca, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<CommissioneBanca> entityClass() {
            return CommissioneBanca.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<CommissioneBanca> {

        public Repository(SqlClient client, java.util.function.Function<io.vertx.mutiny.sqlclient.Row, CommissioneBanca> decoder) {
            super(client, decoder);
        }

        public Uni<CommissioneBanca> getByBonificoExtraSepa(UUID idBonificoExtraSepa) {
            return singleOrNull("SELECT * FROM commissione_banca_a_banca WHERE id_bonifico_extra_sepa = $1", Tuple.of(idBonificoExtraSepa));
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
        public CommissioneBanca getEntity() {
            return CommissioneBanca.this;
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
                            this.bonificoExtraSepa.commissioneBanca = this;
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