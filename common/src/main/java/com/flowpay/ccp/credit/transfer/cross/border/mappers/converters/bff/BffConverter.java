package com.flowpay.ccp.credit.transfer.cross.border.mappers.converters.bff;

import com.flowpay.ccp.credit.transfer.cross.border.errors.MappingError;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.MappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.converters.XmlConverter;
import com.prowidesoftware.swift.model.mx.AbstractMX;
import command.application.global.ec.netgat.gps.eu.AddressTypeEnum;
import command.application.global.ec.netgat.gps.eu.ApplicationCommandType;
import command.application.global.ec.netgat.gps.eu.CommandTypeEnum;
import command.application.global.ec.netgat.gps.eu.ObjectFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import javax.xml.datatype.DatatypeFactory;
import java.io.StringWriter;
import java.util.Date;
import java.util.GregorianCalendar;

@Mapper
public interface BffConverter extends XmlConverter {


    @Override
    default String map(AbstractMX mx, @Context MappingContext context) {
        var objectFactory = new ObjectFactory();
        var applicationCommand = objectFactory.createApplicationCommandType();

        var header = objectFactory.createHeaderType();
        header.setApplicationId(context.bankConfig().bic().substring(0, 8));
        header.setCommandType(CommandTypeEnum.SEND);
        header.setApplicationReference(mx.getAppHdr().reference());

        var address = objectFactory.createAddressType();
        address.setValue(context.bankConfig().bic().substring(0, 8));
        address.setType(AddressTypeEnum.B);
        header.setLocalAddress(address);
        header.setServiceId("T2");
        header.setRequestType(mx.getAppHdr().messageName());

        var now = new GregorianCalendar();
        now.setTime(new Date(mx.getAppHdr().creationDate().toInstant().toEpochMilli()));
        header.setTimestamp(DatatypeFactory.newDefaultInstance().newXMLGregorianCalendar(now));

        applicationCommand.setHeader(header);
        var dataType = objectFactory.createDataType();
        var xmlData = objectFactory.createXmlDataType();
        xmlData.getAny().add(mx.getAppHdr().element());
        xmlData.getAny().add(mx.element());
        dataType.setXmlData(xmlData);
        applicationCommand.setData(dataType);

        var command = objectFactory.createGlobalApplicationCommand(applicationCommand);
        try {
            var instance = JAXBContext.newInstance(JAXBElement.class, ApplicationCommandType.class);
            var marshaller = instance.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            var stringWriter = new StringWriter();
            marshaller.marshal(command, stringWriter);
            return stringWriter.toString();
        } catch (JAXBException e) {
            throw new MappingError(e);
        }

    }
}
