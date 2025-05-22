package com.flowpay.ccp.credit.transfer.cross.border.persistence.accredito;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.SottoTipologiaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.inquiries.ParametriRicerca;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.inquiries.ParametriRicercaBonificiInIngressoBanca;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.inquiries.ParametriRicercaBonificiInIngressoClientela;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.RegolamentoCommissione;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.fee.TipologiaCommissioni;
import com.flowpay.ccp.pagination.persistence.Page;
import com.flowpay.ccp.pagination.persistence.PaginatedEntity;
import com.flowpay.ccp.pagination.persistence.PaginatedRepository;
import com.flowpay.ccp.persistence.*;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;
import java.util.function.Function;


@Table(BonificoInIngresso.table)
public record BonificoInIngresso(
    @Column("id")
    UUID id,

    @Column("id_mappatura_bonifico_in_ingresso")
    UUID idMappaturaBonificoInIngresso,

    @Column("id_bonifico_collegato")
    UUID idBonificoCollegato,

    @Column("is_banca_a_banca")
    Boolean isBancaABanca,

    String tid,

    UUID uetr,

    @Column("bic_ordinante")
    String bicOrdinante,

    @Column("data_ricezione")
    @DateKind
    Instant dataRicezione,

    @Column("sotto_tipologia_bonifico")
    SottoTipologiaBonifico sottoTipologiaBonifico,

    @Column("sistema_di_regolamento")
    SistemaDiRegolamento sistemaDiRegolamento,

    String divisa,

    BigDecimal importo,

    @Column("intestazione_beneficiario")
    String intestazioneBeneficiario,

    @Column("numero_rapporto_beneficiario")
    String numeroRapportoBeneficiario,

    @Column("bic_beneficiario")
    String bicBeneficiario,

    @Column("bic_banca_emittente")
    String bicBancaEmittente,

    @Column("data_regolamento_banca_beneficiario")
    @DateKind(DateKind.DateKindEnum.DATE)
    LocalDate dataRegolamentoBancaBeneficiario,

    @Column("codice_filiale")
    Long codiceFiliale,

    @Column("intestazione_ordinante")
    String intestazioneOrdinante,

    @Column("numero_rapporto_ordinante")
    String numeroRapportoOrdinante,

    @Column("id_rapporto_di_copertura")
    UUID idRapportoDiCopertura,

    @Column("id_rapporto_beneficiario")
    UUID idRapportoBeneficiario,

    String stato,

    @Column("tipologia_commissioni_banca")
    TipologiaCommissioni tipologiaCommissioniBanca,

    @Column("regolamento_commissioni_banca")
    RegolamentoCommissione regolamentoCommissioniBanca,

    @Column("regolamento_commissioni_clientela")
    RegolamentoCommissione regolamentoCommissioniClientela,

    @Ignorable
    @Column("raw_xml")
    String rawXML,

    @Ignorable
    @CreationTimeStamp
    @Column("created_at")
    @DateKind
    Instant createdAt
) {

    static final String table = "bonifico_in_ingresso";

    public static final class Entity extends PaginatedEntity<BonificoInIngresso, Repository> {

        public Entity() {
            super(new Delegate());
        }

        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(BonificoInIngresso.table, sqlClient, this::from);
        }
    }

    public static class Delegate implements com.flowpay.ccp.persistence.Entity<BonificoInIngresso, Repository> {
        @Override
        public Repository repository(SqlClient sqlClient) {
            return new Repository(BonificoInIngresso.table, sqlClient, this::from);
        }

        @Override
        public Class<BonificoInIngresso> entityClass() {
            return BonificoInIngresso.class;
        }
    }

    public static final class Repository extends PaginatedRepository<BonificoInIngresso> {

        private static final Logger LOG = Logger.getLogger(Repository.class);

        public Repository(String tableName, SqlClient client, Function<Row, BonificoInIngresso> decoder) {
            super(tableName, client, decoder);
        }

        public Uni<BonificoInIngresso> getById(UUID id) {
            return single("SELECT * FROM bonifico_in_ingresso WHERE id = $1", Tuple.of(id));
        }

        public Uni<Page<BonificoInIngresso>> searchBonificiInIngresso(
                ParametriRicercaBonificiInIngressoClientela parametri,
                int page,
                int pageSize
        ) {
            return doSearchBonificiInIngresso(
                    parametri,
                    false,
                    page,
                    pageSize);
        }

        public Uni<Page<BonificoInIngresso>> searchBonificiInIngresso(
                ParametriRicercaBonificiInIngressoBanca parametri,
                int page,
                int pageSize
        ) {
            return doSearchBonificiInIngresso(
                    parametri,
                    true,
                    page,
                    pageSize);
        }

        private Uni<Page<BonificoInIngresso>> doSearchBonificiInIngresso(
                ParametriRicerca parametri,
                Boolean bancaABanca,
                int page,
                int pageSize
        ) {
            var selectClause =
                    """
                    SELECT
                        id,
                        id_mappatura_bonifico_in_ingresso,
                        id_bonifico_collegato,
                        is_banca_a_banca,
                        tid,
                        uetr,
                        bic_ordinante,
                        data_ricezione,
                        sotto_tipologia_bonifico,
                        sistema_di_regolamento,
                        divisa,
                        importo,
                        intestazione_beneficiario,
                        numero_rapporto_beneficiario,
                        bic_beneficiario,
                        bic_banca_emittente,
                        data_regolamento_banca_beneficiario,
                        codice_filiale,
                        intestazione_ordinante,
                        numero_rapporto_ordinante,
                        id_rapporto_di_copertura,
                        id_rapporto_beneficiario,
                        stato,
                        tipologia_commissioni_banca,
                        regolamento_commissioni_banca,
                        regolamento_commissioni_clientela
                    """;

            var query = parametri.queryBuilder()
                    .addCondition("is_banca_a_banca = $", bancaABanca)
                    .whereQuery();

            return this.paginate(selectClause, query.getValue0(), query.getValue1(), "created_at DESC", page, pageSize);
        }
    }



}
