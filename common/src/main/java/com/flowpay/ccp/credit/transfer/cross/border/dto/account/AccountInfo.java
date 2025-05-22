package com.flowpay.ccp.credit.transfer.cross.border.dto.account;


import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;

@JsonDeserialize(using = AccountInfoDeserializer.class)
@Schema(
    title = "Informazioni account",
    description = """
    Informazioni su un account.
    
    L'account può essere sia un account IBAN, che un account ID.""",
    oneOf = {
        IBANAccountInfo.class,
        IDAccountInfo.class
    }
)
public interface AccountInfo {

   /*  static AccountInfo from(BonificoExtraSepa creditTransfer) {
        return creditTransfer.creditorIBAN() != null ?
                new IBANAccountInfo(creditTransfer.creditorIBAN()) :
                new IDAccountInfo(creditTransfer.creditorOtherID());
    }

    static AccountInfo from(String iban, String otherID) {
        return iban != null ?
                new IBANAccountInfo(iban) :
                new IDAccountInfo(otherID);
    } */
}
