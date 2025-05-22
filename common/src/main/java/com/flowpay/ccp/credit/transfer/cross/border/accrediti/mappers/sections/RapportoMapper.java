package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.Rapporto;
import com.prowidesoftware.swift.model.mx.dic.AccountIdentification4Choice;
import com.prowidesoftware.swift.model.mx.dic.CashAccount38;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface RapportoMapper {


    @Mapping(target = "numero", ignore = true)
    @Mapping(target = "identificativo", source = "id")
    @Mapping(target = "divisa", source = "ccy")
    @Mapping(target = "codiceTipoConto", source = "tp.cd")
    @Mapping(target = "dettaglioTipoConto", source = "tp.prtry")
    @Mapping(target = "intestazioneConto", source = "nm")
    @Mapping(target = "codiceTipoAlias", source = "prxy.tp.cd")
    @Mapping(target = "descrizioneAlias", source = "prxy.tp.prtry")
    @Mapping(target = "dettaglioIdentificativoAlias", source = "prxy.id")
    @Mapping(target = "codiceIdentificativoConto", source = "id.othr.schmeNm.cd")
    @Mapping(target = "descrizioneIdentificativoConto", source = "id.othr.schmeNm.prtry")
    @Mapping(target = "emittente", source = "id.othr.issr")
    @Mapping(target = "codiceFiliale", ignore = true)
    @Mapping(target = "denominazioneFiliale", ignore = true)
    @Mapping(target = "tipo", ignore = true)
    @Mapping(target = "ndg", ignore = true)
    Rapporto map(CashAccount38 cashAccount);

    default String identificativo(AccountIdentification4Choice identification4Choice) {
        var iban = identification4Choice.getIBAN();
        if (iban != null) {
            return iban;
        }
        return identification4Choice.getOthr().getId();
    }

}
