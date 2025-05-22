package com.flowpay.ccp.credit.transfer.cross.border.mappers.app_headers;

import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.prowidesoftware.swift.model.mx.AppHdr;

public interface AppHeaderMapper {

    AppHdr map(BonificoExtraSepa.WithLinkedEntities bonifico, MappingContext context);
}
