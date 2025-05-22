package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.istitution_identification;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.OptionalMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.intermediary.InformazioniIntermediario;
import com.prowidesoftware.swift.model.mx.dic.ClearingSystemIdentification2Choice;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MxMappingConfig.class,
        imports = OptionalMapper.class)
@DecoratedWith(ClearingSystemIdentification2ChoiceMapper.Decorator.class)
public interface ClearingSystemIdentification2ChoiceMapper {

    @Mapping(target = "cd", source = "codiceSistemaClearing")
    @Mapping(target = "prtry", ignore = true)
    ClearingSystemIdentification2Choice map(InformazioniIntermediario informazioni);

    abstract class Decorator implements ClearingSystemIdentification2ChoiceMapper {
        private final ClearingSystemIdentification2ChoiceMapper delegate;

        Decorator(ClearingSystemIdentification2ChoiceMapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public ClearingSystemIdentification2Choice map(InformazioniIntermediario informazioni) {
            return Utils.allFieldsEmpty(delegate.map(informazioni));
        }
    }
}
