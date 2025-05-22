package com.flowpay.ccp.credit.transfer.cross.border.accrediti;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.AccreditiPersistenceMapper;
import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.AccreditoMapper;
import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.Pacs008ToAccredito;
import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.Pacs008ToPersistence;
import org.mapstruct.factory.Mappers;

public class Pacs008AccreditoMapper implements MapperAccredito {
    @Override
    public AccreditiPersistenceMapper fromXmlToDBMapper() {
        return Mappers.getMapper(Pacs008ToPersistence.class);
    }

    @Override
    public AccreditoMapper fromXmlToJson() {
        return Mappers.getMapper(Pacs008ToAccredito.class);
    }
}
