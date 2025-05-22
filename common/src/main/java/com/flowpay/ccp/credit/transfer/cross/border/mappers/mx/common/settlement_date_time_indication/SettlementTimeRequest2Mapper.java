package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.settlement_date_time_indication;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.settlement_system.InformazioniSistemaDiRegolamento;
import com.prowidesoftware.swift.model.mx.dic.SettlementTimeRequest2;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.OffsetTime;
import java.time.ZoneId;

@Mapper(
        config = MxMappingConfig.class,
        imports = {
                OffsetTime.class,
                ZoneId.class
        }
)
@DecoratedWith(SettlementTimeRequest2Mapper.Decorator.class)
public interface SettlementTimeRequest2Mapper {

    @Mapping(
            target = "CLSTm",
            expression = "java(this.fromInstant(informazioni.orarioAccredito()))")
    @Mapping(
            target = "tillTm",
            expression = "java(this.fromInstant(informazioni.a()))"
    )
    @Mapping(
            target = "frTm",
            expression = "java(this.fromInstant(informazioni.da()))"
    )
    @Mapping(
            target = "rjctTm",
            expression = "java(this.fromInstant(informazioni.scadenzaUltima()))"
    )
    SettlementTimeRequest2 map(InformazioniSistemaDiRegolamento informazioni);

    default OffsetTime fromInstant(Instant instant) {
        if (instant == null) {
            return null;
        }
        return OffsetTime.ofInstant(instant, ZoneId.of("Europe/Rome"));
    }

    abstract class Decorator implements SettlementTimeRequest2Mapper {
        private final SettlementTimeRequest2Mapper delegate;

        Decorator(SettlementTimeRequest2Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public SettlementTimeRequest2 map(InformazioniSistemaDiRegolamento informazioni) {
            return Utils.allFieldsEmpty(delegate.map(informazioni));
        }
    }
}
