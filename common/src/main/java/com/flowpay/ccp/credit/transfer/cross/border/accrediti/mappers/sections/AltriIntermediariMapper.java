package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.AccreditoMappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.AltriIntermediari;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.Intermediario;
import com.prowidesoftware.swift.model.mx.MxPacs00800108;
import com.prowidesoftware.swift.model.mx.dic.CreditTransferTransaction39;
import com.prowidesoftware.swift.model.mx.dic.FIToFICustomerCreditTransferV08;
import com.prowidesoftware.swift.model.mx.dic.GroupHeader93;
import com.prowidesoftware.swift.model.mx.dic.SettlementInstruction7;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;

@Mapper(uses = {
        RapportoMapper.class
})
public interface AltriIntermediariMapper {

    @Mapping(target = "riferimentiCorrispondenteMittente", source = "FIToFICstmrCdtTrf.grpHdr.sttlmInf.sttlmAcct")
    @Mapping(target = "bancaCorrispondenteMittente", source = ".", qualifiedByName = "bancaCorrispondenteMittente")
    @Mapping(target = "bancaCorrispondenteRicevente", source = ".", qualifiedByName = "bancaCorrispondenteRicevente")
    @Mapping(target = "istitutoTerzoDiRimborso", source = ".", qualifiedByName = "istitutoTerzoDiRimborso")
    @Mapping(target = "bancaIstruttrice1", source = ".", qualifiedByName = "bancaIstruttrice1")
    @Mapping(target = "bancaIstruttrice2", source = ".", qualifiedByName = "bancaIstruttrice2")
    @Mapping(target = "bancaIstruttrice3", source = ".", qualifiedByName = "bancaIstruttrice3")
    @Mapping(target = "bancaIntermediaria1", source = ".", qualifiedByName = "bancaIntermediaria1")
    @Mapping(target = "bancaIntermediaria2", source = ".", qualifiedByName = "bancaIntermediaria2")
    @Mapping(target = "bancaIntermediaria3", source = ".", qualifiedByName = "bancaIntermediaria3")
    AltriIntermediari map(MxPacs00800108 pacs, @Context AccreditoMappingContext context);

    default Optional<SettlementInstruction7> settlementInstruction(MxPacs00800108 pacs) {
        return Optional.ofNullable(pacs).map(MxPacs00800108::getFIToFICstmrCdtTrf)
        .map(FIToFICustomerCreditTransferV08::getGrpHdr)
        .map(GroupHeader93::getSttlmInf);
    }

    default Optional<CreditTransferTransaction39> creditTransferInfo(MxPacs00800108 pacs) {
        return Optional.ofNullable(pacs).map(MxPacs00800108::getFIToFICstmrCdtTrf)
                .map(FIToFICustomerCreditTransferV08::getCdtTrfTxInf)
                .map(element -> {
                    if (element.isEmpty()) {
                        return null;
                    }
                    return element.get(0);
                });
    }

    @Named("bancaCorrispondenteMittente")
    default Intermediario bancaCorrispondenteMittente(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return IntermediarioMapper.INSTANCE.map(settlementInstruction(pacs), SettlementInstruction7::getInstgRmbrsmntAgt, SettlementInstruction7::getInstgRmbrsmntAgtAcct, context);
    }

    @Named("bancaCorrispondenteRicevente")
    default Intermediario bancaCorrispondenteRicevente(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return IntermediarioMapper.INSTANCE.map(settlementInstruction(pacs), SettlementInstruction7::getInstdRmbrsmntAgt, SettlementInstruction7::getInstdRmbrsmntAgtAcct, context);

    }

    @Named("istitutoTerzoDiRimborso")
    default Intermediario istitutoTerzoDiRimborso(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return IntermediarioMapper.INSTANCE.map(settlementInstruction(pacs), SettlementInstruction7::getThrdRmbrsmntAgt, SettlementInstruction7::getThrdRmbrsmntAgtAcct, context);
    }



    @Named("bancaIstruttrice1")
    default Intermediario bancaIstruttrice1(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return IntermediarioMapper.INSTANCE.map(creditTransferInfo(pacs), CreditTransferTransaction39::getPrvsInstgAgt1, CreditTransferTransaction39::getPrvsInstgAgt1Acct, context);
    }

    @Named("bancaIstruttrice2")
    default Intermediario bancaIstruttrice2(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return IntermediarioMapper.INSTANCE.map(creditTransferInfo(pacs), CreditTransferTransaction39::getPrvsInstgAgt2, CreditTransferTransaction39::getPrvsInstgAgt2Acct, context);

    }

    @Named("bancaIstruttrice3")
    default Intermediario bancaIstruttrice3(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return IntermediarioMapper.INSTANCE.map(creditTransferInfo(pacs), CreditTransferTransaction39::getPrvsInstgAgt3, CreditTransferTransaction39::getPrvsInstgAgt3Acct, context);
    }

    @Named("bancaIntermediaria1")
    default Intermediario bancaIntermediaria1(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return IntermediarioMapper.INSTANCE.map(creditTransferInfo(pacs), CreditTransferTransaction39::getIntrmyAgt1, CreditTransferTransaction39::getIntrmyAgt1Acct, context);
    }

    @Named("bancaIntermediaria2")
    default Intermediario bancaIntermediaria2(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return IntermediarioMapper.INSTANCE.map(creditTransferInfo(pacs), CreditTransferTransaction39::getIntrmyAgt2, CreditTransferTransaction39::getIntrmyAgt2Acct, context);
    }
    @Named("bancaIntermediaria3")
    default Intermediario bancaIntermediaria3(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return IntermediarioMapper.INSTANCE.map(creditTransferInfo(pacs), CreditTransferTransaction39::getIntrmyAgt3, CreditTransferTransaction39::getIntrmyAgt3Acct, context);
    }
}
