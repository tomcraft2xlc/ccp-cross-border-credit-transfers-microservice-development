package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.party_identification;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.party_identification.Privato;
import com.prowidesoftware.swift.model.mx.dic.GenericPersonIdentification1;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(GenericPersonIdentification1Mapper.Decorator.class)
public interface GenericPersonIdentification1Mapper {

    @Mapping(target = "id", source = "identificativo")
    @Mapping(target = "schmeNm", expression = "java(PersonIdentificationSchemeName1ChoiceMapper.INSTANCE.map(codiceIdentificativo, codiceProprietario))")
    @Mapping(target = "issr", source = "emittente")
    GenericPersonIdentification1 map(
            String identificativo,
            String codiceIdentificativo,
            String codiceProprietario,
            String emittente);


    default List<GenericPersonIdentification1> mapList(Privato privato) {
        var result = new ArrayList<GenericPersonIdentification1>(2);
        result.add(
                this.map(
                        privato.identificativoSoggetto1(),
                        privato.codiceIdentificativoSoggetto1(),
                        privato.codiceProprietarioIdentificativoSoggetto1(),
                        privato.emittente1()
                )
        );

        result.add(
                this.map(
                        privato.identificativoSoggetto2(),
                        privato.codiceIdentificativoSoggetto2(),
                        privato.codiceProprietarioIdentificativoSoggetto2(),
                        privato.emittente2()
                )
        );

        return result;
    }

    abstract class Decorator implements GenericPersonIdentification1Mapper {

        private final GenericPersonIdentification1Mapper delegate;

        Decorator(GenericPersonIdentification1Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public GenericPersonIdentification1 map(String identificativo, String codiceIdentificativo, String codiceProprietario, String emittente) {
            return Utils.allFieldsEmpty(delegate.map(identificativo, codiceIdentificativo, codiceProprietario, emittente));
        }
    }
}
