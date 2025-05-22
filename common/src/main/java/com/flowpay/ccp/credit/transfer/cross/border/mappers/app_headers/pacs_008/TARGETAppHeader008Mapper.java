package com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers.pacs_008;

import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers.AppHeaderMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers.HeaderMapping;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party.Party9ChoiceMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.prowidesoftware.swift.model.mx.BusinessAppHdrV01;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = Party9ChoiceMapper.class)
public interface TARGETAppHeader008Mapper extends AppHeaderMapper {

    @Override
    @HeaderMapping
    @Mapping(target = "fr", expression = "java(Party9ChoiceMapper.INSTANCE.map(context.bankConfig(), context))")
    @Mapping(target = "to", source = ".")
    @Mapping(target = "bizMsgIdr", source = "entity.tid")
    @Mapping(target = "msgDefIdr", constant = "pacs.008.001.08")
    @Mapping(target = "bizSvc", ignore = true)
    BusinessAppHdrV01 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context);

}
