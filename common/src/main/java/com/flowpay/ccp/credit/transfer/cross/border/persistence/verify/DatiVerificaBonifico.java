package com.flowpay.ccp.credit.transfer.cross.border.persistence.verify;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.flowpay.ccp.credit.transfer.cross.border.persistence.EntityWithLinkedEntities;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.persistence.Column;
import com.flowpay.ccp.persistence.DateKind;
import com.flowpay.ccp.persistence.EnumKind;
import com.flowpay.ccp.persistence.Table;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

/**
 * Rappresenta un processo di verifica di un bonifico extra-SEPA.
 * <p>
 * Questa classe viene mappata sulla tabella {@code dati_verifica_bonifico} nel
 * database
 * e traccia il processo di verifica del bonifico.
 * 
 * SQL per la creazione della tabella:
 * 
 * <pre>{@code
 * CREATE TABLE dati_verifica_bonifico (
 *     id UUID PRIMARY KEY,
 *     id_bonifico_extra_sepa UUID NOT NULL,
 *     inizio_verifica TIMESTAMP WITH TIME ZONE NOT NULL,
 *     stato_verifica stato_verifica NOT NULL,
 *     stato_verifica_saldo_rapporto stato_verifica_saldo_rapporto NOT NULL,
 *     importo_sconfinamento NUMERIC(18,2),
 *     stato_verifica_avvertenze_rapporto stato_verifica_generico NOT NULL,
 *     stato_verifica_embargo stato_verifica_embargo NOT NULL,
 *     stato_verifica_cambio stato_verifica_cambio NOT NULL,
 *     stato_verifica_holiday_table_paese stato_verifica_generico NOT NULL,
 *     stato_verifica_holiday_table_divisa stato_verifica_generico NOT NULL,
 *     stato_verifica_bonifico stato_verifica_bonifico NOT NULL,
 * 
 *     -- Vincolo di chiave esterna
 *     CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
 *         REFERENCES bonifico_extra_sepa(id)
 * );
 * }</pre>
 */
@Table("dati_verifica_bonifico")
public record DatiVerificaBonifico(
        /**
         * Identificativo univoco del record.
         * 
         * Corrisponde anche al PROCESS_ID del worker
         */
        UUID id,

        /** Identificativo del bonifico extra-SEPA associato */
        @Column("id_bonifico_extra_sepa") UUID idBonificoExtraSepa,

        @DateKind @Column("inizio_verifica") Instant inizioVerifica,

        /** Stato globale della verifica */
        @EnumKind @Column("stato_verifica") StatoVerifica statoVerifica,

        /*
         * The rest of the record is split into steps, and will be filled as the calls
         * complete
         */

        // 1.a - Recupera Saldo Rapporto

        /** Stati della verifica saldo rapporto */
        @EnumKind @Column("stato_verifica_saldo_rapporto") StatoVerificaSaldoRapporto statoVerificaSaldoRapporto,

        /** Importo sconfinamento, se necessario confermare */
        @Column("importo_sconfinamento") BigDecimal importoSconfinamento,

        // 1.b - Verifica Avvertenze Rapporto

        /** Stati della verifica avvertenze rapporto */
        @EnumKind @Column("stato_verifica_avvertenze_rapporto") StatoVerificaAvvertenze statoVerificaAvvertenzeRapporto,

        // 1.c - Verifica Embargo

        /** Stati della verifica embargo */
        @EnumKind @Column("stato_verifica_embargo") StatoVerificaEmbargo statoVerificaEmbargo,

        // 1.d - Verifica Cambio

        @EnumKind @Column("stato_verifica_cambio") StatoVerificaCambio statoVerificaCambio,

        // 1.e - Verifica Holiday Table (per paese)

        @EnumKind @Column("stato_verifica_holiday_table_paese") StatoVerificaGenerico statoVerificaHolidayTablePaese,

        // 1.f - Verifica Holiday Table (per divisa)

        @EnumKind @Column("stato_verifica_holiday_table_divisa") StatoVerificaGenerico statoVerificaHolidayTableDivisa,

        // 2 - Verifica Bonifico

        @EnumKind @Column("stato_verifica_bonifico") StatoVerificaBonifico statoVerificaBonifico) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<DatiVerificaBonifico, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<DatiVerificaBonifico> entityClass() {
            return DatiVerificaBonifico.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<DatiVerificaBonifico> {
        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, DatiVerificaBonifico> decoder) {
            super(client, decoder);
        }

        public Uni<DatiVerificaBonifico> getByBonificoExtraSepa(UUID idBonificoExtraSepa) {
            return singleOrNull("SELECT * FROM dati_verifica_bonifico WHERE id_bonifico_extra_sepa = $1",
                    Tuple.of(idBonificoExtraSepa));
        }

        public Uni<DatiVerificaBonifico> getById(UUID id) {
            return single("SELECT * FROM dati_verifica_bonifico WHERE id = $1", Tuple.of(id));
        }
        public Uni<DatiVerificaBonifico> getByIdForUpdate(UUID id) {
            return single("SELECT * FROM dati_verifica_bonifico WHERE id = $1 FOR UPDATE", Tuple.of(id));
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
        public Collection<DatiVerificaBonificoAvvertenza.WithLinkedEntities> avvertenze;
        public Collection<DatiVerificaBonificoErroreTecnico.WithLinkedEntities> erroriTecnici;

        @Override
        public DatiVerificaBonifico getEntity() {
            return DatiVerificaBonifico.this;
        }

        @Override
        public UUID id() {
            return getEntity().id();
        }

        @Override
        public void collectLinked(Consumer<EntityWithLinkedEntities> consumer) {
            consumer.accept(bonificoExtraSepa);
            if (avvertenze != null)
                avvertenze.forEach(consumer);
            if (erroriTecnici != null)
                erroriTecnici.forEach(consumer);
        }

        @Override
        public Multi<EntityWithLinkedEntities> loadChilds(SqlClient sqlClient) {
            List<Multi<EntityWithLinkedEntities>> multis = new ArrayList<>();

            if (this.bonificoExtraSepa == null) {
                multis.add(new BonificoExtraSepa.Entity()
                        .repository(sqlClient).getById(idBonificoExtraSepa())
                        .map(linked -> {
                            this.bonificoExtraSepa = linked.withLinkedEntities();
                            this.bonificoExtraSepa.datiVerificaBonifico = this;
                            return this.bonificoExtraSepa;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.bonificoExtraSepa));
            }

            if (this.avvertenze == null) {
                this.avvertenze = new ArrayList<>();
                multis.add(new DatiVerificaBonificoAvvertenza.Entity()
                        .repository(sqlClient).getAllByDatiVerificaBonifico(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.datiVerificaBonifico = this;
                            this.avvertenze.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.avvertenze).onItem()
                        .castTo(EntityWithLinkedEntities.class));
            }

            if (this.erroriTecnici == null) {
                this.erroriTecnici = new ArrayList<>();
                multis.add(new DatiVerificaBonificoErroreTecnico.Entity()
                        .repository(sqlClient).getAllByDatiVerificaBonifico(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.datiVerificaBonifico = this;
                            this.erroriTecnici.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.erroriTecnici).onItem()
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

    
    /**
     * Check if all the calls have finished.
     * 
     * @return true if all the calls have finished, false otherwise
     */    
    public boolean allCallsEnded() {
        return Stream.<Supplier<VerifyCallStatus>>of(
                this::statoVerificaSaldoRapporto,
                this::statoVerificaAvvertenzeRapporto,
                this::statoVerificaEmbargo,
                this::statoVerificaCambio,
                this::statoVerificaHolidayTablePaese,
                this::statoVerificaHolidayTableDivisa,
                this::statoVerificaBonifico)
                .map(Supplier::get)
                .noneMatch(VerifyCallStatus::isWaitingForAnswer) ||
                // aggiungo casistica per verifica andata in errore a seguito di una risposta
                // ricevuta da cabel
                this.statoVerifica != StatoVerifica.ATTENDE_RISPOSTE;
    }


    /**
     * Check if all the calls were successful.
     * 
     * @return true if all the calls were successful, false otherwise
     */
    public boolean allCallsWereSuccessful() {
        return Stream.<Supplier<VerifyCallStatus>>of(
                this::statoVerificaSaldoRapporto,
                this::statoVerificaAvvertenzeRapporto,
                this::statoVerificaEmbargo,
                this::statoVerificaCambio,
                this::statoVerificaHolidayTablePaese,
                this::statoVerificaHolidayTableDivisa,
                this::statoVerificaBonifico)
                .map(Supplier::get)
                .allMatch(VerifyCallStatus::isVerified);
    }
}
