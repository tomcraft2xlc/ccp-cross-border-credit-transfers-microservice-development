package com.flowpay.ccp.credit.transfer.cross.border.mappers;

import com.flowpay.ccp.credit.transfer.cross.border.Tipologia;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers.AppHeaderMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.converters.XmlConverter;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.prowidesoftware.swift.model.mx.AbstractMX;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public interface ISOMapper {

    AppHeaderMapper headerMapper(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context);

    MxMapper mxMapper(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context);

    XmlConverter converter(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context);

    default FileName fileName(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context, AbstractMX mx) {
        return new FileName(
                bonifico.getEntity().sistemaDiRegolamento(),
                context.bankConfig(),
                LocalDate.now(),
                bonifico.getEntity().tid());
    }

    default Tipologia fileType(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context, AbstractMX mx) {
        return bonifico.getEntity().sistemaDiRegolamento().tipologia();
    }

    default ISOData map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context) {
        var mx = mxMapper(bonifico, context).map(bonifico, context);
        mx.setAppHdr(headerMapper(bonifico, context).map(bonifico, context));
        return new ISOData(converter(bonifico, context).map(mx, context), this.fileName(bonifico, context, mx).formatted(), this.fileType(bonifico, context, mx));
    }

    record ISOData(
        String xml,
        String fileName,
        Tipologia tipologia
    ) { }

    record FileName(
            SistemaDiRegolamento sistemaDiRegolamento,
            BanksConfig.BankConfig bankConfig,
            LocalDate date,
            String tid
    ) {
        private String formatted() {
            return "%s%s%s%s.xml".formatted(
                    sistemaDiRegolamento.famiglia(),
                    bankConfig.bic().substring(0,8),
                    date.format(DateTimeFormatter.ofPattern("yyMMdd")),
                    tid
            );
        }
    }

}
