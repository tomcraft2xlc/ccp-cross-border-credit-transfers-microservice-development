package com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers.pacs_009;

import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers.AppHeaderMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers.HeaderMapping;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party.Party44ChoiceMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party.Party9ChoiceMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.prowidesoftware.swift.model.mx.BusinessAppHdrV01;
import com.prowidesoftware.swift.model.mx.dic.Party44Choice;
import com.prowidesoftware.swift.model.mx.dic.Party9Choice;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = Party9ChoiceMapper.class,
imports = Party9ChoiceMapper.class)
public interface TARGETAppHeader009Mapper extends AppHeaderMapper {

    @Override
    @HeaderMapping
    @Mapping(target = "fr", expression = "java(Party9ChoiceMapper.INSTANCE.map(context.bankConfig(), context))")
    @Mapping(target = "to", source = ".", qualifiedByName = "to")
    @Mapping(target = "bizMsgIdr", source = ".", qualifiedByName = "bizMsgIdr")
    @Mapping(target = "msgDefIdr", source = ".", qualifiedByName = "msgDefIdr")
    @Mapping(target = "bizSvc", ignore = true)
    BusinessAppHdrV01 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context);

    @Named("msgDefIdr")
    default String msgDefIdr(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context) {
        if (Boolean.TRUE.equals(context.isCove())) {
            //TODO: restituire valore corretto
            return "pacs.009.001.08COV";
        }
        return  "pacs.009.001.08CORE";
    }

    @Named("bizMsgIdr")
    default String bizMsgIdr(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context) {
        if (Boolean.TRUE.equals(context.isCove())) {
            return bonifico.getEntity().tidDocumentoCollegato();
        }
        return bonifico.getEntity().tid();
    }

    @Named("to")
    default Party9Choice to(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context) {
        if (Boolean.TRUE.equals(context.isCove())) {
            return Party9ChoiceMapper.INSTANCE.mapWithIntermediario(bonifico, context, TipoIntermediario.BANCA_CORRISPONDENTE_MITTENTE);
        }
        return Party9ChoiceMapper.INSTANCE.mapWithIntermediario(bonifico, context, TipoIntermediario.BANCA_DESTINATARIA);
    }
}
