package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.ordinante;

import java.util.List;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.actor.Attore;

import jakarta.validation.Valid;

public record InfoOrdinante(
        Attore infoAttore,
        List<@Valid InfoNDG> presentatore,
        List<@Valid InfoNDG> titolareEffettivo) {
}
