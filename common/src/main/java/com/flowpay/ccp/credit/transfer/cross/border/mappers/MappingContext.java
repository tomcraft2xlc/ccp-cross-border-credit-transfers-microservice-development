package com.flowpay.ccp.credit.transfer.cross.border.mappers;

import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;

public record MappingContext(
        Boolean isCove,
        Boolean stp,
        BanksConfig.BankConfig bankConfig
) {
}
