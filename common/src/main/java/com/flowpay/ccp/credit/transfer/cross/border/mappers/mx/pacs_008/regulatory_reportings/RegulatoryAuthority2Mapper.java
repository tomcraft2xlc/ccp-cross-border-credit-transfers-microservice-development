package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.regulatory_reportings;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.RegulatoryReporting;
import com.prowidesoftware.swift.model.mx.dic.RegulatoryAuthority2;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(RegulatoryAuthority2Mapper.Decorator.class)
public interface RegulatoryAuthority2Mapper {

    @Mapping(target = "nm", source = "autoritaRichiedente")
    @Mapping(target = "ctry", source = "paeseAutoritaRichiedente")
    RegulatoryAuthority2 map(RegulatoryReporting reporting);

    abstract class Decorator implements RegulatoryAuthority2Mapper {

        private final RegulatoryAuthority2Mapper delegate;

        Decorator(RegulatoryAuthority2Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public RegulatoryAuthority2 map(RegulatoryReporting reporting) {
            return Utils.allFieldsEmpty(delegate.map(reporting));
        }
    }
}
