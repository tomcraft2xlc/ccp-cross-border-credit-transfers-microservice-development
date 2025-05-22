package com.flowpay.ccp.credit.transfer.cross.border.dto.credit_transfer;

import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;

public record RichiestaModificaBonificoStato(
        CreditTransferStatus nuovoStato
) {
}
