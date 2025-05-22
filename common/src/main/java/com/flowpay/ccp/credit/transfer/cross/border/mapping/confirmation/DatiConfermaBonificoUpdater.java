package com.flowpay.ccp.credit.transfer.cross.border.mapping.confirmation;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.DatiConfermaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.StatoConferma;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.StatoConfermaAvvertenze;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.StatoConfermaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.StatoConfermaCambio;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.StatoConfermaEmbargo;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.StatoConfermaGenerico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.StatoConfermaSaldoRapporto;

@Mapper(config = MappingCommonConfig.class)
public interface DatiConfermaBonificoUpdater {
    @Mapping(target = "statoConfermaSaldoRapporto", source = "statoConfermaSaldoRapporto")
    @Mapping(target = "importoSconfinamento", source = "importoSconfinamento")
    DatiConfermaBonifico updateStatoConfermaSaldoRapporto(DatiConfermaBonifico datiConferma,
            StatoConfermaSaldoRapporto statoConfermaSaldoRapporto, BigDecimal importoSconfinamento);

    @Mapping(target = "statoConfermaAvvertenzeRapporto", source = "statoConfermaAvvertenzeRapporto")
    DatiConfermaBonifico updateStatoConfermaAvvertenzeRapporto(DatiConfermaBonifico datiConferma,
            StatoConfermaAvvertenze statoConfermaAvvertenzeRapporto);

    @Mapping(target = "statoConfermaEmbargo", source = "statoConfermaEmbargo")
    DatiConfermaBonifico updateStatoConfermaEmbargo(DatiConfermaBonifico datiConfermaBonifico,
            StatoConfermaEmbargo statoConfermaEmbargo);

    @Mapping(target = "statoConfermaCambio", source = "statoConfermaCambio")
    DatiConfermaBonifico updateStatoConfermaCambio(DatiConfermaBonifico datiConferma,
            StatoConfermaCambio statoConfermaCambio);

    @Mapping(target = "statoConfermaHolidayTablePaese", source = "stato")
    DatiConfermaBonifico updateStatoConfermaHolidayTablePaese(DatiConfermaBonifico datiConferma,
            StatoConfermaGenerico stato);

    @Mapping(target = "statoConfermaHolidayTableDivisa", source = "stato")
    DatiConfermaBonifico updateStatoConfermaHolidayTableDivisa(DatiConfermaBonifico datiConferma,
            StatoConfermaGenerico stato);

    @Mapping(target = "statoConfermaBonifico", source = "statoConfermaBonifico")
    DatiConfermaBonifico updateStatoConfermaBonifico(DatiConfermaBonifico datiConferma, StatoConfermaBonifico statoConfermaBonifico);

    @Mapping(target = "statoConferma", source = "statoConferma")
    DatiConfermaBonifico updateStatoConferma(DatiConfermaBonifico datiConferma, StatoConferma statoConferma);

}