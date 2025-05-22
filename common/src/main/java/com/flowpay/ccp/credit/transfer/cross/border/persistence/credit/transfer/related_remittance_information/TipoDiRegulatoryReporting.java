package com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information;

import com.prowidesoftware.swift.model.mx.dic.RegulatoryReportingType1Code;

public enum TipoDiRegulatoryReporting {
    CREDITORE,
    DEBITORE,
    ENTRAMBI;

    public RegulatoryReportingType1Code asRegulatoryReportingType1Code() {
        return switch (this) {
            case CREDITORE -> RegulatoryReportingType1Code.CRED;
            case DEBITORE -> RegulatoryReportingType1Code.DEBT;
            case ENTRAMBI -> RegulatoryReportingType1Code.BOTH;
        };
    }
}
