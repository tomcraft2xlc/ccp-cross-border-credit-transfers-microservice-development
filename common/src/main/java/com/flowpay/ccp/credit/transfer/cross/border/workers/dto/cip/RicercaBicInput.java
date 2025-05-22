package com.flowpay.ccp.credit.transfer.cross.border.workers.dto.cip;

public record RicercaBicInput(
    String codiceBic,
    String descrizioneBic,
    Integer abi
) {
}