package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.header;

import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "creDtTm", source = "bonifico.entity.istanteCreazioneMessaggio")
@Mapping(target = "btchBookg", ignore = true)
@Mapping(target = "nbOfTxs", constant = "1")
@Mapping(target = "ctrlSum", ignore = true)
@Mapping(target = "ttlIntrBkSttlmAmt", ignore = true)
@Mapping(target = "intrBkSttlmDt", ignore = true)
@Mapping(target = "sttlmInf", source = ".")
@Mapping(target = "pmtTpInf", ignore = true)
@Mapping(target = "instgAgt", ignore = true)
@Mapping(target = "instdAgt", ignore = true)
public @interface GroupHeader93Mapping {
}
