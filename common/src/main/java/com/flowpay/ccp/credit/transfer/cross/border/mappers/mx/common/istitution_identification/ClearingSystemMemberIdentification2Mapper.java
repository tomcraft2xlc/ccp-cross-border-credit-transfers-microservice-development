package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.istitution_identification;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.InformazioniIntermediario;
import com.prowidesoftware.swift.model.mx.dic.ClearingSystemMemberIdentification2;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        uses = ClearingSystemIdentification2ChoiceMapper.class)
@DecoratedWith(ClearingSystemMemberIdentification2Mapper.Decorator.class)
public interface ClearingSystemMemberIdentification2Mapper {

    @Mapping(target = "clrSysId", source = ".")
    @Mapping(target = "mmbId", source = "identificativoClearing")
    ClearingSystemMemberIdentification2 map(InformazioniIntermediario informazioni);

    abstract class Decorator implements ClearingSystemMemberIdentification2Mapper {
        private final ClearingSystemMemberIdentification2Mapper delegate;

        Decorator(ClearingSystemMemberIdentification2Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public ClearingSystemMemberIdentification2 map(InformazioniIntermediario informazioni) {
            return Utils.allFieldsEmpty(delegate.map(informazioni));
        }
    }
}
