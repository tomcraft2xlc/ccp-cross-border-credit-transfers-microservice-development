package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.settlement_system;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.persistence.*;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Rappresenta le informazioni relative al sistema di regolamento di un pagamento,<p>
 * mappato secondo lo schema ISO 20022 {@code SettlementTimeRequest2}.<p>
 *
 * Questa classe memorizza i dettagli relativi alla priorità del regolamento,
 * agli orari di accredito e scadenza, e alle finestre temporali applicabili
 * al pagamento.<p>
 *
 * La tabella associata nel database è {@code informazioni_sistema_di_regolamento}.<p>
 *
 * SQL per la creazione della tabella:<p>
 * <pre>{@code
 * CREATE TABLE informazioni_sistema_di_regolamento (
 *     id UUID PRIMARY KEY,
 *     id_bonifico_extra_sepa UUID NOT NULL,
 *     priorita VARCHAR(255) NOT NULL,
 *     orario_accredito TIMESTAMP WITH TIME ZONE,
 *     da TIMESTAMP WITH TIME ZONE,
 *     a TIMESTAMP WITH TIME ZONE,
 *     scadenza_ultima TIMESTAMP WITH TIME ZONE,
 *     created_at TIMESTAMP WITH TIME ZONE NOT NULL,
 *     stp BOOLEAN NOT NULL,
 *
 *      -- Vincolo di chiave esterna
 *     CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
 *         REFERENCES bonifico_extra_sepa(id) DEFERRABLE
 * );
 * }</pre>
 *
 * @param id               Identificativo univoco del record.<p>
 * @param priorita         Priorità del regolamento del pagamento.<p>Mappato in {@code SttlmPrty} di CreditTransferTransaction39.</p>
 * @param orarioAccredito  Orario di accredito del pagamento.<p>Mappato in {@code CLSTm} di SettlementTimeRequest2.</p>
 * @param da               Orario di inizio del periodo disponibile per il regolamento.<p>Mappato in {@code FrTm} di SettlementTimeRequest2.</p>
 * @param a                Orario di fine del periodo disponibile per il regolamento.<p>Mappato in {@code TillTm} di SettlementTimeRequest2.</p>
 * @param scadenzaUltima   Ultimo tempo disponibile per il regolamento prima del rifiuto.<p>Mappato in {@code RjctTm} di SettlementTimeRequest2.</p>
 *
 * @see Priorita
 * @see <a href="https://www.iso20022.org/">ISO 20022</a>
 */
@Table("informazioni_sistema_di_regolamento")
public record InformazioniSistemaDiRegolamento(

        /// Identificativo univoco del record.
        UUID id,

        @Column("informazioni_documento_collegato")
        Boolean informazioniDocumentoCollegato,

        @Column("id_bonifico_extra_sepa")
        UUID idBonificoExtraSepa,

        /// Priorità del regolamento del pagamento.
        /// <p>Mappato in `SttlmPrty` di SettlementTimeRequest2.</p>
        Priorita priorita,

        /// Orario di accredito del pagamento.
        /// <p>Mappato in `CLSTm` di SettlementTimeRequest2.</p>
        @Column("orario_accredito")
        Instant orarioAccredito,

        /// Orario di inizio del periodo disponibile per il regolamento.
        /// <p>Mappato in `FrTm` di SettlementTimeRequest2.</p>
        Instant da,

        /// Orario di fine del periodo disponibile per il regolamento.
        /// <p>Mappato in `TillTm` di SettlementTimeRequest2.</p>
        Instant a,

        /// Ultimo tempo disponibile per il regolamento prima del rifiuto.
        /// <p>Mappato in `RjctTm` di SettlementTimeRequest2.</p>
        @Column("scadenza_ultima")
        Instant scadenzaUltima,

        /// Genera un messaggio di tipo stp
        Boolean stp,

        /// Timestamp di creazione della registrazione
        @CreationTimeStamp 
        @Column("created_at") 
        @DateKind 
        Instant createdAt
) {

    public static class Entity
            implements com.flowpay.ccp.persistence.Entity<InformazioniSistemaDiRegolamento, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<InformazioniSistemaDiRegolamento> entityClass() {
            return InformazioniSistemaDiRegolamento.class;
        }
    }

    public static class Repository extends com.flowpay.ccp.persistence.Repository<InformazioniSistemaDiRegolamento> {

        public Repository(SqlClient client, Function<Row, InformazioniSistemaDiRegolamento> decoder) {
            super(client, decoder);
        }

        public Uni<InformazioniSistemaDiRegolamento> getByBonificoExtraSepa(UUID id, Boolean documentoCollegato) {
            return singleOrNull("""
                    SELECT * FROM informazioni_sistema_di_regolamento
                    WHERE id_bonifico_extra_sepa = $1 AND informazioni_documento_collegato = $2
                    """, Tuple.of(id, documentoCollegato));
        }
    }

    public WithLinkedEntities withLinkedEntities() {
        return new WithLinkedEntities();
    }

    public class WithLinkedEntities implements EntityWithLinkedEntities {
        public BonificoExtraSepa.WithLinkedEntities bonificoExtraSepa;

        @Override
        public InformazioniSistemaDiRegolamento getEntity() {
            return InformazioniSistemaDiRegolamento.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.bonificoExtraSepa == null) {
                multis.add(new BonificoExtraSepa.Entity()
                        .repository(sqlClient).getById(idBonificoExtraSepa())
                        .map(linked -> {
                            this.bonificoExtraSepa = linked.withLinkedEntities();
                            this.bonificoExtraSepa.informazioniSistemaDiRegolamento = this;
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

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(bonificoExtraSepa);
        }
    }
}
