package com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers.pacs_009;


import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers.AppHeaderMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers.HeaderMapping;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party.Party44ChoiceMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.TipoIntermediario;
import com.prowidesoftware.swift.model.mx.BusinessAppHdrV02;
import com.prowidesoftware.swift.model.mx.dic.Party44Choice;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        uses = Party44ChoiceMapper.class,
imports = {
        Party44ChoiceMapper.class,
        TipoIntermediario.class
})
public interface CBPRAppHeader009Mapper extends AppHeaderMapper {

    @Override
    @HeaderMapping
    @Mapping(target = "fr", expression = "java(Party44ChoiceMapper.INSTANCE.map(context.bankConfig(), context))")
    @Mapping(target = "to", source = ".", qualifiedByName = "to")
    @Mapping(target = "bizMsgIdr", source = ".", qualifiedByName = "bixMsgIdr")
    @Mapping(target = "msgDefIdr", source = ".", qualifiedByName = "msgDefIdr")
    @Mapping(target = "bizSvc", source = ".", qualifiedByName = "bizSvc")
    @Mapping(target = "mktPrctc", ignore = true)
    @Mapping(target = "bizPrcgDt", ignore = true)
    BusinessAppHdrV02 map(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context);

    @Named("msgDefIdr")
    default String msgDefIdr(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context) {
        return "pacs.009.001.08";
    }

    @Named("bizSvc")
    default String bizSvc(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context) {
        return "swift.cbprplus.02";
    }

    @Named("bixMsgIdr")
    default String bixMsgIdr(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context) {
        if (Boolean.TRUE.equals(context.isCove())) {
            return bonifico.getEntity().tidDocumentoCollegato();
        }
        return bonifico.getEntity().tid();
    }

    @Named("to")
    default Party44Choice to(BonificoExtraSepa.WithLinkedEntities bonifico, @Context MappingContext context) {
        if (Boolean.TRUE.equals(context.isCove())) {
            return Party44ChoiceMapper.INSTANCE.mapWithIntermediario(bonifico, context, TipoIntermediario.BANCA_CORRISPONDENTE_MITTENTE);
        }
        return Party44ChoiceMapper.INSTANCE.mapWithIntermediario(bonifico, context, TipoIntermediario.BANCA_DESTINATARIA);
    }
}
