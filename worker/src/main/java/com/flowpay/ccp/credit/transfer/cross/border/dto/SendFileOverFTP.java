package com.flowpay.ccp.credit.transfer.cross.border.dto;

import com.flowpay.ccp.credit.transfer.cross.border.Tipologia;

public record SendFileOverFTP(
        String fileName,
        Tipologia tipologia,
        String b64File
) {
}
