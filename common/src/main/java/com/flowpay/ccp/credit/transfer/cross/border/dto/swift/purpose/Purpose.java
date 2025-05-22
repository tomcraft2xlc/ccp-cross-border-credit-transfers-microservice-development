package com.flowpay.ccp.credit.transfer.cross.border.dto.swift.purpose;


import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;

@JsonDeserialize(using = PurposeDeserializer.class)
@Schema(
    oneOf = {
        CodePurpose.class,
        ProprietaryPurpose.class
    }
)
public interface Purpose {

    /* static Purpose from(BonificoExtraSepa creditTransfer) {
        return creditTransfer.purposeCodeISO() != null ?
                new CodePurpose(creditTransfer.purposeCodeISO()) :
                new ProprietaryPurpose(creditTransfer.purposeCodePrivate());
    } */
}
