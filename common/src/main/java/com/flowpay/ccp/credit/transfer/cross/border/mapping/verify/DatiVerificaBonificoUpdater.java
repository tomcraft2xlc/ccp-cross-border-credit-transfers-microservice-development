package com.flowpay.ccp.credit.transfer.cross.border.mapping.verify;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.DatiVerificaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerifica;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerificaAvvertenze;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerificaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerificaCambio;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerificaEmbargo;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerificaGenerico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.StatoVerificaSaldoRapporto;

@Mapper(config = MappingCommonConfig.class)
public interface DatiVerificaBonificoUpdater {
    @Mapping(target = "statoVerificaSaldoRapporto", source = "statoVerificaSaldoRapporto")
    @Mapping(target = "importoSconfinamento", source = "importoSconfinamento")
    DatiVerificaBonifico updateStatoVerificaSaldoRapporto(DatiVerificaBonifico datiVerifica,
            StatoVerificaSaldoRapporto statoVerificaSaldoRapporto, BigDecimal importoSconfinamento);

    @Mapping(target = "statoVerificaAvvertenzeRapporto", source = "statoVerificaAvvertenzeRapporto")
    DatiVerificaBonifico updateStatoVerificaAvvertenzeRapporto(DatiVerificaBonifico datiVerifica,
            StatoVerificaAvvertenze statoVerificaAvvertenzeRapporto);

    @Mapping(target = "statoVerificaEmbargo", source = "statoVerificaEmbargo")
    DatiVerificaBonifico updateStatoVerificaEmbargo(DatiVerificaBonifico datiVerificaBonifico,
            StatoVerificaEmbargo statoVerificaEmbargo);

    @Mapping(target = "statoVerificaCambio", source = "statoVerificaCambio")
    DatiVerificaBonifico updateStatoVerificaCambio(DatiVerificaBonifico datiVerifica,
            StatoVerificaCambio statoVerificaCambio);

    @Mapping(target = "statoVerificaHolidayTablePaese", source = "stato")
    DatiVerificaBonifico updateStatoVerificaHolidayTablePaese(DatiVerificaBonifico datiVerifica,
            StatoVerificaGenerico stato);

    @Mapping(target = "statoVerificaHolidayTableDivisa", source = "stato")
    DatiVerificaBonifico updateStatoVerificaHolidayTableDivisa(DatiVerificaBonifico datiVerifica,
            StatoVerificaGenerico stato);

    @Mapping(target = "statoVerificaBonifico", source = "statoVerificaBonifico")
    DatiVerificaBonifico updateStatoVerificaBonifico(DatiVerificaBonifico datiVerifica, StatoVerificaBonifico statoVerificaBonifico);

    @Mapping(target = "statoVerifica", source = "statoVerifica")
    DatiVerificaBonifico updateStatoVerifica(DatiVerificaBonifico datiVerifica, StatoVerifica statoVerifica);

}