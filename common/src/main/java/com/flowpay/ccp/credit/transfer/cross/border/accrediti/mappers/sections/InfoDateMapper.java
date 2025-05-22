package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti.InfoDate;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details.DettagliDate;
import com.prowidesoftware.swift.model.mx.MxPacs00800108;
import com.prowidesoftware.swift.model.mx.dic.CreditTransferTransaction39;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Mapper
public interface InfoDateMapper {

    @Mapping(target = "dataCreazione", source = "FIToFICstmrCdtTrf.grpHdr.creDtTm")
    @Mapping(target = "dataEsecuzione", expression = "java(LocalDate.now())")
    @Mapping(target = "dataRegolamentoBancaBeneficiario", source = "FIToFICstmrCdtTrf.cdtTrfTxInf")
    InfoDate map(MxPacs00800108 pacs);

    default Instant map(OffsetDateTime dateTime) {
        return dateTime.toInstant();
    }

    default LocalDate map(List<CreditTransferTransaction39> credits) {
        if (credits == null) {
            return null;
        }
        if (credits.isEmpty()) {
            return null;
        }
        return map(credits.get(0));
    }

    default LocalDate map(CreditTransferTransaction39 credit) {
        if (credit != null) {
            return credit.getIntrBkSttlmDt();
        }
        return null;
    }
}
