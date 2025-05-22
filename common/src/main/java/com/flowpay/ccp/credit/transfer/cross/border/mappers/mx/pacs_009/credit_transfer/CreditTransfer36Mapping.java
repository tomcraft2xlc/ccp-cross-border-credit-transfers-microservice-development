package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_009.credit_transfer;

import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.credit_transfer_transaction.InterBankSettlementAmount;
import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "intrBkSttlmAmt", source = ".", qualifiedBy = InterBankSettlementAmount.class)
@Mapping(target = "intrBkSttlmDt", source = "entity.dataRegolamentoBancaBeneficiario")
@Mapping(target = "sttlmTmIndctn", ignore = true)
@Mapping(target = "ultmtDbtr", ignore = true)
@Mapping(target = "ultmtCdtr", ignore = true)
public @interface CreditTransfer36Mapping {
}
