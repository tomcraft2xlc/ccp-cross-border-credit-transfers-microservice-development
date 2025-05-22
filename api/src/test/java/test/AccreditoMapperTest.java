package test;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.SottoTipologiaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.AccreditoMapper;
import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.AccreditoMappingContext;
import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.Pacs008ToAccredito;
import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections.IntermediarioMapper;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti.Accredito;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.Intermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.prowidesoftware.swift.model.mx.AbstractMX;
import com.prowidesoftware.swift.model.mx.BusinessAppHdrV02;
import com.prowidesoftware.swift.model.mx.MxPacs00800108;
import com.prowidesoftware.swift.model.mx.dic.FinancialInstitutionIdentification18;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class AccreditoMapperTest {

    final String xmlString = "<RequestPayload><AppHdr xmlns=\"urn:iso:std:iso:20022:tech:xsd:head.001.001.02\"><Fr><FIId><FinInstnId><BICFI>CRACIT33XXX</BICFI></FinInstnId></FIId></Fr><To><FIId><FinInstnId><BICFI>CIPBITMMXXX</BICFI></FinInstnId></FIId></To><BizMsgIdr>14320086072</BizMsgIdr><MsgDefIdr>pacs.008.001.08</MsgDefIdr><BizSvc>swift.cbprplus.03</BizSvc><CreDt>2025-05-08T09:06:25.875292+00:00</CreDt></AppHdr><Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pacs.008.001.08\"><FIToFICstmrCdtTrf><GrpHdr><MsgId>14320086072</MsgId><CreDtTm>2025-05-08T09:06:25.875292+00:00</CreDtTm><NbOfTxs>1</NbOfTxs><SttlmInf><SttlmMtd>INDA</SttlmMtd></SttlmInf></GrpHdr><CdtTrfTxInf><PmtId><InstrId>14320086072</InstrId><EndToEndId>14320086072</EndToEndId><UETR>e4e793d0-3bd4-44c4-b82b-1e63d65da5d1</UETR></PmtId><PmtTpInf><SvcLvl><Prtry>SDVA</Prtry></SvcLvl></PmtTpInf><IntrBkSttlmAmt Ccy=\"USD\">40.00</IntrBkSttlmAmt><IntrBkSttlmDt>2025-05-09</IntrBkSttlmDt><InstdAmt Ccy=\"USD\">40.00</InstdAmt><ChrgBr>SHAR</ChrgBr><InstgAgt><FinInstnId><BICFI>CRACIT33XXX</BICFI></FinInstnId></InstgAgt><InstdAgt><FinInstnId><BICFI>CIPBITMMXXX</BICFI></FinInstnId></InstdAgt><Dbtr><Nm>TOFFALINI STEFANO</Nm><PstlAdr><StrtNm>VIA VENTOTTO GENNAIO, 35</StrtNm><PstCd>37136</PstCd><TwnNm>VERONA</TwnNm><CtrySubDvsn>VR</CtrySubDvsn><Ctry>IT</Ctry></PstlAdr></Dbtr><DbtrAcct><Id><IBAN>IT47D0326661620000012217204</IBAN></Id><Ccy>EUR</Ccy></DbtrAcct><DbtrAgt><FinInstnId><BICFI>CRACIT33XXX</BICFI><LEI>8156007395B20763EB44</LEI><Nm>BANCA DI CREDITO COOPERATIVO DI CAMBIANO</Nm><PstlAdr><StrtNm>VIALE ANTONIO GRAMSCI 34</StrtNm><PstCd>50132</PstCd><TwnNm>FIRENZE</TwnNm><CtrySubDvsn>FI</CtrySubDvsn><Ctry>IT</Ctry></PstlAdr></FinInstnId></DbtrAgt><CdtrAgt><FinInstnId><BICFI>IFISIT2VXXX</BICFI><LEI>8156005420362AE59184</LEI><Nm>BANCA IFIS SPA</Nm><PstlAdr><StrtNm>VIA GATTA 11 MESTRE</StrtNm><PstCd>30174</PstCd><TwnNm>VENEZIA</TwnNm><CtrySubDvsn>VE</CtrySubDvsn><Ctry>IT</Ctry></PstlAdr></FinInstnId></CdtrAgt><Cdtr><Nm>MARCO ROSSI</Nm><PstlAdr><StrtNm>VIA ROSSI</StrtNm><PstCd>00000</PstCd><TwnNm>20</TwnNm><Ctry>HK</Ctry></PstlAdr></Cdtr><CdtrAcct><Id><Othr><Id>1234567890</Id></Othr></Id></CdtrAcct><InstrForCdtrAgt><InstrInf>PROVA 1234567</InstrInf></InstrForCdtrAgt><InstrForCdtrAgt><InstrInf>PROVA 123456789</InstrInf></InstrForCdtrAgt><InstrForNxtAgt><InstrInf>PROVA 12345</InstrInf></InstrForNxtAgt><RmtInf><Ustrd>PROVA 1234</Ustrd></RmtInf></CdtTrfTxInf></FIToFICstmrCdtTrf></Document></RequestPayload>";

    final AccreditoMappingContext mappingContext = new AccreditoMappingContext(
            SistemaDiRegolamento.NO_TARGET,
            SottoTipologiaBonifico.PACS_008
    );

    @Test
    void testAccreditoMapper() {
        var mxPacs = (MxPacs00800108) AbstractMX.parse(xmlString);
        assertNotNull(mxPacs.getFIToFICstmrCdtTrf());

        var businessAppHdr = (BusinessAppHdrV02) mxPacs.getAppHdr();
        assertNotNull(businessAppHdr);

        FinancialInstitutionIdentification18 finInsId = businessAppHdr.getTo().getFIId().getFinInstnId();
        IntermediarioMapper intermediarioMapper = Mappers.getMapper(IntermediarioMapper.class);
        Intermediario bancaDestinataria = intermediarioMapper.map(finInsId, null, mappingContext);
        assertEquals("CIPBITMMXXX", bancaDestinataria.bic());

        AccreditoMapper accreditoMapper = Mappers.getMapper(Pacs008ToAccredito.class);
        Accredito accredito = accreditoMapper.map(mxPacs, mappingContext);
        assertNotNull(accredito);
    }
}
