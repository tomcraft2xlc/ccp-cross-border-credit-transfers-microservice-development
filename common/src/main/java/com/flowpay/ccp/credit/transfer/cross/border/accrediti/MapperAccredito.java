package com.flowpay.ccp.credit.transfer.cross.border.accrediti;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.AccreditiPersistenceMapper;
import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.AccreditoMapper;

public interface MapperAccredito {

    AccreditiPersistenceMapper fromXmlToDBMapper();

    AccreditoMapper fromXmlToJson();
}
