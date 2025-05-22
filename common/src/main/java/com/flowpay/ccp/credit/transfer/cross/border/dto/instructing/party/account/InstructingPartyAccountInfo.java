package com.flowpay.ccp.credit.transfer.cross.border.dto.instructing.party.account;

import com.flowpay.ccp.credit.transfer.cross.border.AccountType;

//TODO: define the fields
public record InstructingPartyAccountInfo(
        AccountType accountType,
        String id
) {
}
