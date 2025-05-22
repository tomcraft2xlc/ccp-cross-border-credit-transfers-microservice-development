package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax.DettagliRecordDettagliFiscali;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.tax.RecordDettagliFiscali;
import com.prowidesoftware.swift.model.mx.dic.TaxPeriod2;
import com.prowidesoftware.swift.model.mx.dic.TaxRecordPeriod1Code;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(TaxPeriod2Mapper.Decorator.class)
public interface TaxPeriod2Mapper {

    @Mapping(target = "yr", source = "entity.annoRiferimentoDichiarazione")
    @Mapping(target = "tp", expression = "java(map(dettagli.getEntity().periodoRiferimentoDichiarazione()))")
    @Mapping(target = "frToDt.frDt", source = "entity.periodoRiferimentoDichiarazioneDa")
    @Mapping(target = "frToDt.toDt", source = "entity.periodoRiferimentoDichiarazioneA")
    TaxPeriod2 map(RecordDettagliFiscali.WithLinkedEntities dettagli);

    @Mapping(target = "yr", source = "entity.annoRiferimento")
    @Mapping(target = "tp", expression = "java(map(dettagli.getEntity().periodoRiferimento()))")
    @Mapping(target = "frToDt.frDt", source = "entity.periodoRiferimentoDa")
    @Mapping(target = "frToDt.toDt", source = "entity.periodoRiferimentoA")
    TaxPeriod2 map(DettagliRecordDettagliFiscali.WithLinkedEntities dettagli);

    default LocalDate fromYr(String year) {
        return LocalDate.parse(year, DateTimeFormatter.ofPattern("yyyy"));
    }

    default TaxRecordPeriod1Code map(String raw) {
        if (raw != null) {
            try {
                return TaxRecordPeriod1Code.fromValue(raw);
            } catch (IllegalArgumentException ignored) {

            }
        }
        return null;
    }

    abstract class Decorator implements TaxPeriod2Mapper {

        private final TaxPeriod2Mapper delegate;

        Decorator(TaxPeriod2Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public TaxPeriod2 map(RecordDettagliFiscali.WithLinkedEntities dettagli) {
            return Utils.allFieldsEmpty(delegate.map(dettagli));
        }
    }
}
