package com.flowpay.ccp.credit.transfer.cross.border.dto.authorization;

import java.util.List;

public record ListaAutorizzazioniBonifico(
        Boolean notificaNegata,
        List<DettaglioAutorizzazioniBonifico> dettaglio
) {
}
