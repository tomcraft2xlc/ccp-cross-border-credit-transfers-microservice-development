package com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation;

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
 * Rappresenta un processo di conferma di un bonifico extra-SEPA.
 * <p>
 * Questa classe viene mappata sulla tabella {@code dati_conferma_bonifico} nel
 * database
 * e traccia il processo di conferma del bonifico.
 * 
 * SQL per la creazione della tabella:
 * 
 * <pre>{@code
 * CREATE TABLE dati_conferma_bonifico (
 *     id UUID PRIMARY KEY,
 *     id_bonifico_extra_sepa UUID NOT NULL,
 *     inizio_conferma TIMESTAMP WITH TIME ZONE NOT NULL,
 *     stato_conferma stato_conferma NOT NULL,
 *     stato_conferma_saldo_rapporto stato_conferma_saldo_rapporto NOT NULL,
 *     importo_sconfinamento NUMERIC(18,2),
 *     stato_conferma_avvertenze_rapporto stato_conferma_generico NOT NULL,
 *     stato_conferma_embargo stato_conferma_embargo NOT NULL,
 *     flag_forzatura_embargo BOOLEAN NOT NULL,
 *     stato_conferma_cambio stato_conferma_cambio NOT NULL,
 *     stato_conferma_holiday_table_paese stato_conferma_generico NOT NULL,
 *     stato_conferma_holiday_table_divisa stato_conferma_generico NOT NULL,
 *     stato_conferma_bonifico stato_conferma_bonifico NOT NULL,
 *     numero_transazione INTEGER,
 * 
 *     -- Vincolo di chiave esterna
 *     CONSTRAINT fk_bonifico_extra_sepa FOREIGN KEY (id_bonifico_extra_sepa)
 *         REFERENCES bonifico_extra_sepa(id)
 * );
 * }</pre>
 */
@Table("dati_conferma_bonifico")
public record DatiConfermaBonifico(
        /**
         * Identificativo univoco del record.
         * 
         * Corrisponde anche al PROCESS_ID del worker
         */
        UUID id,

        /** Identificativo del bonifico extra-SEPA associato */
        @Column("id_bonifico_extra_sepa")
        UUID idBonificoExtraSepa,

        @DateKind
        @Column("inizio_conferma")
        Instant inizioConferma,

        /** Stato globale della conferma */
        @EnumKind
        @Column("stato_conferma")
        StatoConferma statoConferma,

        /*
         * The rest of the record is split into steps, and will be filled as the calls
         * complete
         */

        // 1.a - Recupera Saldo Rapporto

        /** Stati della conferma saldo rapporto */
        @EnumKind
        @Column("stato_conferma_saldo_rapporto")
        StatoConfermaSaldoRapporto statoConfermaSaldoRapporto,

        /** Importo sconfinamento, se necessario confermare */
        @Column("importo_sconfinamento")
        BigDecimal importoSconfinamento,

        // 1.b - Conferma Avvertenze Rapporto

        /** Stati della conferma avvertenze rapporto */
        @EnumKind
        @Column("stato_conferma_avvertenze_rapporto")
        StatoConfermaAvvertenze statoConfermaAvvertenzeRapporto,

        // 1.c - Conferma Embargo

        /** Stati della conferma embargo */
        @EnumKind
        @Column("stato_conferma_embargo")
        StatoConfermaEmbargo statoConfermaEmbargo,

        // 1.d - Conferma Cambio

        @EnumKind
        @Column("stato_conferma_cambio")
        StatoConfermaCambio statoConfermaCambio,

        // 1.e - Conferma Holiday Table (per paese)

        @EnumKind
        @Column("stato_conferma_holiday_table_paese")
        StatoConfermaGenerico statoConfermaHolidayTablePaese,

        // 1.f - Conferma Holiday Table (per divisa)

        @EnumKind
        @Column("stato_conferma_holiday_table_divisa")
        StatoConfermaGenerico statoConfermaHolidayTableDivisa,

        // 2 - Conferma Bonifico

        @EnumKind
        @Column("stato_conferma_bonifico")
        StatoConfermaBonifico statoConfermaBonifico) {

    public static final class Entity implements com.flowpay.ccp.persistence.Entity<DatiConfermaBonifico, Repository> {

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(sqlClient, this::from);
        }

        @Override
        public Class<DatiConfermaBonifico> entityClass() {
            return DatiConfermaBonifico.class;
        }
    }

    public static final class Repository extends com.flowpay.ccp.persistence.Repository<DatiConfermaBonifico> {
        public Repository(SqlClient client,
                java.util.function.Function<io.vertx.mutiny.sqlclient.Row, DatiConfermaBonifico> decoder) {
            super(client, decoder);
        }

        public Uni<DatiConfermaBonifico> getByBonificoExtraSepa(UUID idBonificoExtraSepa) {
            return singleOrNull("SELECT * FROM dati_conferma_bonifico WHERE id_bonifico_extra_sepa = $1",
                    Tuple.of(idBonificoExtraSepa));
        }

        public Uni<DatiConfermaBonifico> getById(UUID id) {
            return single("SELECT * FROM dati_conferma_bonifico WHERE id = $1", Tuple.of(id));
        }
        public Uni<DatiConfermaBonifico> getByIdForUpdate(UUID id) {
            return single("SELECT * FROM dati_conferma_bonifico WHERE id = $1 FOR UPDATE", Tuple.of(id));
        }
        public Uni<DatiConfermaBonifico> getByBonificoExtraSepaForUpdate(UUID idBonificoExtraSepa) {
            return single("SELECT * FROM dati_conferma_bonifico WHERE id_bonifico_extra_sepa = $1 FOR UPDATE", Tuple.of(idBonificoExtraSepa));
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
        public Collection<DatiConfermaBonificoAvvertenza.WithLinkedEntities> avvertenze;
        public Collection<DatiConfermaBonificoErroreTecnico.WithLinkedEntities> erroriTecnici;

        @Override
        public DatiConfermaBonifico getEntity() {
            return DatiConfermaBonifico.this;
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
                            this.bonificoExtraSepa.datiConfermaBonifico = this;
                            return this.bonificoExtraSepa;
                        })
                        .onItem().castTo(EntityWithLinkedEntities.class)
                        .toMulti());
            } else {
                multis.add(Multi.createFrom().item(this.bonificoExtraSepa));
            }

            if (this.avvertenze == null) {
                this.avvertenze = new ArrayList<>();
                multis.add(new DatiConfermaBonificoAvvertenza.Entity()
                        .repository(sqlClient).getAllByDatiConfermaBonifico(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.datiConfermaBonifico = this;
                            this.avvertenze.add(withLinked);
                            return withLinked;
                        }));
            } else {
                multis.add(Multi.createFrom().iterable(this.avvertenze).onItem()
                        .castTo(EntityWithLinkedEntities.class));
            }

            if (this.erroriTecnici == null) {
                this.erroriTecnici = new ArrayList<>();
                multis.add(new DatiConfermaBonificoErroreTecnico.Entity()
                        .repository(sqlClient).getAllByDatiConfermaBonifico(id())
                        .map(linked -> {
                            var withLinked = linked.withLinkedEntities();
                            withLinked.datiConfermaBonifico = this;
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
    public boolean allStep1CallsEnded() {
        return Stream.<Supplier<ConfirmationCallStatus>>of(
                this::statoConfermaSaldoRapporto,
                this::statoConfermaAvvertenzeRapporto,
                this::statoConfermaEmbargo,
                this::statoConfermaCambio,
                this::statoConfermaHolidayTablePaese,
                this::statoConfermaHolidayTableDivisa)
                .map(Supplier::get)
                .noneMatch(ConfirmationCallStatus::isWaitingForAnswer) ||
                // aggiungo casistica per verifica andata in errore a seguito di una risposta
                // ricevuta da cabel
                this.statoConferma != StatoConferma.ATTENDE_RISPOSTE;
    }


    /**
     * Check if all the calls were successful.
     * 
     * @return true if all the calls were successful, false otherwise
     */
    public boolean allStep1CallsWereSuccessful() {
        return Stream.<Supplier<ConfirmationCallStatus>>of(
                this::statoConfermaSaldoRapporto,
                this::statoConfermaAvvertenzeRapporto,
                this::statoConfermaEmbargo,
                this::statoConfermaCambio,
                this::statoConfermaHolidayTablePaese,
                this::statoConfermaHolidayTableDivisa)
                .map(Supplier::get)
                .allMatch(ConfirmationCallStatus::isConfirmed);
    }

    public Boolean step2CallEnded() {
        return this.statoConfermaBonifico != StatoConfermaBonifico.NON_INVIATA && !this.statoConfermaBonifico.isWaitingForAnswer();
    }
}
