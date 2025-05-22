package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.AccreditoMappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti.DettagliIdentificativiBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.settlement_system.Priorita;
import com.prowidesoftware.swift.model.mx.AppHdr;
import com.prowidesoftware.swift.model.mx.MxPacs00800108;
import com.prowidesoftware.swift.model.mx.dic.CreditTransferTransaction39;
import com.prowidesoftware.swift.model.mx.dic.GroupHeader93;
import com.prowidesoftware.swift.model.mx.dic.PaymentIdentification7;
import com.prowidesoftware.swift.model.mx.dic.SettlementDateTimeIndication1;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.OffsetDateTime;

@Mapper(config = MxMappingConfig.class,
imports = Priorita.class,
uses = InfoSistemaDiRegolamentoMapper.class)
public interface DettagliIdentificativiBonificoMapper {

    default DettagliIdentificativiBonifico map(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        var appHdr = pacs.getAppHdr();
        var cdtTrfTxInf = pacs.getFIToFICstmrCdtTrf().getCdtTrfTxInf().get(0);
        var identification = cdtTrfTxInf.getPmtId();
        var groupHeader = pacs.getFIToFICstmrCdtTrf().getGrpHdr();

        var dettaglio = this.create(appHdr);
        dettaglio = this.update(dettaglio, groupHeader);
        dettaglio = this.update(dettaglio, pacs, context);
        return dettaglio;
    }

    @Mapping(target = "identificativoFlusso", expression = "java(appHdr.reference())")
    @Mapping(target = "possibileDuplicato", expression = "java(appHdr.duplicate())")
    @Mapping(target = "identificativoDiDefinizioneDelMessaggio", expression = "java(appHdr.messageName())")
    @Mapping(target = "servizioDiBusiness", expression = "java(appHdr.serviceName())")
    @Mapping(target = "dataDiCreazioneFlusso", expression = "java(from(appHdr.creationDate()))")
    @Mapping(target = "tid", ignore = true)
    @Mapping(target = "codiceUETR", ignore = true)
    @Mapping(target = "endToEnd", ignore = true)
    @Mapping(target = "idMessaggio", ignore = true)
    @Mapping(target = "idTransazione", ignore = true)
    @Mapping(target = "dataAddebito", ignore = true)
    @Mapping(target = "dataAccredito", ignore = true)
    @Mapping(target = "infoSistemaDiRegolamento", ignore = true)
    @Mapping(target = "numeroTransazioni", ignore = true)
    @Mapping(target = "idSistemaDiClearing", ignore = true)
    @Mapping(target = "canaleDiClearing", ignore = true)
    @Mapping(target = "metodoDiRegolamento", ignore = true)
    DettagliIdentificativiBonifico create(AppHdr appHdr);



    @Mapping(target = "idMessaggio", source = "groupHeader93.msgId")
    @Mapping(target = "numeroTransazioni", source = "groupHeader93.nbOfTxs")
    @Mapping(target = "metodoDiRegolamento", source = "groupHeader93.sttlmInf.sttlmMtd")
    DettagliIdentificativiBonifico update(DettagliIdentificativiBonifico dettagliIdentificativiBonifico, GroupHeader93 groupHeader93);

    @Mapping(target = "idSistemaDiClearing", source = "creditTransferTransaction39.pmtId.clrSysRef")
    @Mapping(target = "canaleDiClearing", source = "creditTransferTransaction39.pmtTpInf.clrChanl")
    @Mapping(target = "tid", source = "creditTransferTransaction39.pmtId.instrId")
    @Mapping(target = "codiceUETR", source = "creditTransferTransaction39.pmtId.UETR")
    @Mapping(target = "endToEnd", source = "creditTransferTransaction39.pmtId.endToEndId")
    @Mapping(target = "idTransazione", source = "creditTransferTransaction39.pmtId.txId")
    DettagliIdentificativiBonifico update(DettagliIdentificativiBonifico dettagliIdentificativiBonifico, CreditTransferTransaction39 creditTransferTransaction39);

    @Mapping(target = "dataAddebito", source = "settlementDateTimeIndication1.dbtDtTm")
    @Mapping(target = "dataAccredito", source = "settlementDateTimeIndication1.cdtDtTm")
    DettagliIdentificativiBonifico update(DettagliIdentificativiBonifico dettagliIdentificativiBonifico, SettlementDateTimeIndication1 settlementDateTimeIndication1);

    @Mapping(target = "infoSistemaDiRegolamento", source = "pacs", qualifiedByName = "create")
    DettagliIdentificativiBonifico update(DettagliIdentificativiBonifico dettagliIdentificativiBonifico, MxPacs00800108 pacs, @Context AccreditoMappingContext context);

    default Instant from(OffsetDateTime dateTime) {
        return dateTime.toInstant();
    }
}
