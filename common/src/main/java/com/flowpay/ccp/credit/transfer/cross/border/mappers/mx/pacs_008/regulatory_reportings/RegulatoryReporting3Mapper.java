package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.regulatory_reportings;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.RegulatoryReporting;
import com.prowidesoftware.swift.model.mx.dic.RegulatoryReporting3;
import com.prowidesoftware.swift.model.mx.dic.RegulatoryReportingType1Code;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        uses = {
        RegulatoryAuthority2Mapper.class,
        StructuredRegulatoryReporting3Mapper.class
})
@DecoratedWith(RegulatoryReporting3Mapper.Decorator.class)
public interface RegulatoryReporting3Mapper {

    @Mapping(target = "dbtCdtRptgInd", expression = "java(mapRegulatoryReporitingType1Code(info))")
    @Mapping(target = "authrty", source = "entity")
    @Mapping(target = "dtls", source = "dettagliRegulatoryReportings")
    RegulatoryReporting3 map(RegulatoryReporting.WithLinkedEntities info);

    default RegulatoryReportingType1Code mapRegulatoryReporitingType1Code(RegulatoryReporting.WithLinkedEntities info) {
        var result = info.getEntity().tipo();
        if (result != null) {
            return result.asRegulatoryReportingType1Code();
        }
        return null;
    }

    abstract class Decorator implements RegulatoryReporting3Mapper {

        private final RegulatoryReporting3Mapper delegate;

        Decorator(RegulatoryReporting3Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public RegulatoryReporting3 map(RegulatoryReporting.WithLinkedEntities info) {
            return Utils.allFieldsEmpty(delegate.map(info));
        }
    }
}
