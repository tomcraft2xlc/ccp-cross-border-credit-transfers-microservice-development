package com.flowpay.ccp.credit.transfer.cross.border.mappers;

import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers.AppHeaderMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers.pacs_008.CBPRAppHeader008Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers.pacs_008.TARGETAppHeader008Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.converters.XmlConverter;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.converters.bff.BffConverter;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.converters.cbpr.CBPRConverter;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.MxPacs00800108Mapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.prowidesoftware.swift.model.mx.AbstractMX;
import org.mapstruct.factory.Mappers;

public class Pacs008ISOMapper implements ISOMapper {

    @Override
    public AppHeaderMapper headerMapper(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context) {
        if (bonifico.getEntity().sistemaDiRegolamento() == SistemaDiRegolamento.TARGET) {
            return Mappers.getMapper(TARGETAppHeader008Mapper.class);
        } else {
            return Mappers.getMapper(CBPRAppHeader008Mapper.class);
        }
    }

    @Override
    public MxMapper mxMapper(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context) {
        return Mappers.getMapper(MxPacs00800108Mapper.class);
    }

    @Override
    public XmlConverter converter(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context) {
        BanksConfig.BankConfig.SettlementInfo.Detail detail;
        if (bonifico.getEntity().sistemaDiRegolamento() == SistemaDiRegolamento.TARGET) {
            detail = context.bankConfig().channel().t2();
        } else {
            detail = context.bankConfig().channel().cbpr();
        }

        if (detail.tramitatoDa().isPresent()) {
            return Mappers.getMapper(BffConverter.class);
        } else {
            return Mappers.getMapper(CBPRConverter.class);
        }
    }
}
