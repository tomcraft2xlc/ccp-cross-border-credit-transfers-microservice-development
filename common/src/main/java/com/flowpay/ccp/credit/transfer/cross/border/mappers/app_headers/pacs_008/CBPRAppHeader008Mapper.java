package com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers.pacs_008;


import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers.AppHeaderMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers.HeaderMapping;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party.Party44ChoiceMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.prowidesoftware.swift.model.mx.BusinessAppHdrV02;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = Party44ChoiceMapper.class)
public interface CBPRAppHeader008Mapper extends AppHeaderMapper {

    @Override
    @HeaderMapping
    @Mapping(target = "fr", expression = "java(Party44ChoiceMapper.INSTANCE.map(context.bankConfig(), context))")
    @Mapping(target = "to", source = ".")
    @Mapping(target = "bizMsgIdr", source = "entity.tid")
    @Mapping(target = "msgDefIdr", constant = "pacs.008.001.08")
    @Mapping(target = "bizSvc", expression = "java(bizSvc(bonifico))")
    @Mapping(target = "mktPrctc", ignore = true)
    @Mapping(target = "bizPrcgDt", ignore = true)
    BusinessAppHdrV02 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context);

    default String bizSvc(BonificoExtraSepa.WithLinkedEntities bonifico) {
        if (Boolean.TRUE.equals(bonifico.informazioniSistemaDiRegolamento.getEntity().stp())) {
            return "swift.cbprplus.stp.02";
        }
        return "swift.cbprplus.02";
    }

}
