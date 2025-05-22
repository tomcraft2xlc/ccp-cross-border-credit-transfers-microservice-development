package com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.sections.*;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti.Accredito;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti.AttoreIdentificato;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.Intermediario;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import com.prowidesoftware.swift.model.mx.*;
import com.prowidesoftware.swift.model.mx.dic.CreditTransferTransaction39;
import com.prowidesoftware.swift.model.mx.dic.FIToFICustomerCreditTransferV08;
import com.prowidesoftware.swift.model.mx.dic.FinancialInstitutionIdentification18;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;


@Mapper(uses = {
        TipoBonificoMapper.class,
        DettagliIdentificativiBonificoMapper.class,
        DettagliBonificoMapper.class,
        AltriIntermediariMapper.class,
})
public interface Pacs008ToAccredito extends AccreditoMapper {


    @Override
    default Accredito map(AbstractMX message, @Context AccreditoMappingContext mappingContext) {
        var actual = (MxPacs00800108) message;
        return this.map(actual, mappingContext);
    }

    @Mapping(target = "tipoBonifico", source = ".")
    @Mapping(target = "bancaDestinataria", source = ".", qualifiedByName = "bancaDestinataria")
    @Mapping(target = "bancaRegolanteRicevente", source = ".", qualifiedByName = "bancaRegolanteRicevente")
    @Mapping(target = "dettagliIdentificativiBonifico", source = ".")
    @Mapping(target = "beneficiario", source = ".", qualifiedByName = "beneficiario")
    @Mapping(target = "beneficiarioEffettivo", source = ".", qualifiedByName = "beneficiarioEffettivo")
    @Mapping(target = "bancaDelBeneficiario", source = ".", qualifiedByName = "bancaDelBeneficiario")
    @Mapping(target = "ordinante", source = ".", qualifiedByName = "ordinante")
    @Mapping(target = "soggettoIstruttore", source = ".", qualifiedByName = "soggettoIstruttore")
    @Mapping(target = "ordinanteEffettivo", source = ".", qualifiedByName = "ordinanteEffettivo")
    @Mapping(target = "bancaMittente", source = ".", qualifiedByName = "bancaMittente")
    @Mapping(target = "bancaRegolanteMittente", source = ".", qualifiedByName = "bancaRegolanteMittente")
    @Mapping(target = "bancaDellOrdinante", source = ".", qualifiedByName = "bancaDellOrdinante")
    @Mapping(target = "altriIntermediari", source = ".")
    @Mapping(target = "dettagliBonifico", source = ".")
    @Mapping(target = "infoStato", source = ".")
    Accredito map(MxPacs00800108 message, @Context AccreditoMappingContext context);

    default Optional<CreditTransferTransaction39> getCreditTransfer(MxPacs00800108 pacs) {
        return Optional.ofNullable(pacs)
        .map(MxPacs00800108::getFIToFICstmrCdtTrf)
        .map(FIToFICustomerCreditTransferV08::getCdtTrfTxInf)
        .map(credits -> {
            if (credits.isEmpty()) {
                return null;
            }
            return credits.get(0);
        });
    }

    @Named("bancaDestinataria")
    default Intermediario bancaDestinataria(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        if (context.sistemaDiRegolamento() == SistemaDiRegolamento.TARGET) {
            return IntermediarioMapper.INSTANCE.map(((BusinessAppHdrV01) pacs.getAppHdr()).getTo().getFIId());
        } else {
            return IntermediarioMapper.INSTANCE.map(((BusinessAppHdrV02) pacs.getAppHdr()).getTo().getFIId());
        }
    }

    @Named("bancaMittente")
    default Intermediario bancaMittente(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        if (context.sistemaDiRegolamento() == SistemaDiRegolamento.TARGET) {
            return IntermediarioMapper.INSTANCE.map(((BusinessAppHdrV01) pacs.getAppHdr()).getFr().getFIId());
        } else {
            return IntermediarioMapper.INSTANCE.map(((BusinessAppHdrV02) pacs.getAppHdr()).getFr().getFIId());
        }
    }

    @Named("bancaRegolanteRicevente")
    default Intermediario bancaRegolanteRicevente(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return IntermediarioMapper.INSTANCE.map(getCreditTransfer(pacs), CreditTransferTransaction39::getInstdAgt, null, context);
    }

    @Named("beneficiario")
    default AttoreIdentificato beneficiario(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return AttoreIdentificatoMapper.INSTANCE.map(getCreditTransfer(pacs), CreditTransferTransaction39::getCdtr, CreditTransferTransaction39::getCdtrAcct);
    }

    @Named("beneficiarioEffettivo")
    default AttoreIdentificato beneficiarioEffettivo(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return AttoreIdentificatoMapper.INSTANCE.map(getCreditTransfer(pacs), CreditTransferTransaction39::getUltmtCdtr, null);
    }

    @Named("bancaDelBeneficiario")
    default Intermediario bancaDelBeneficiario(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return IntermediarioMapper.INSTANCE.map(getCreditTransfer(pacs), CreditTransferTransaction39::getCdtrAgt, CreditTransferTransaction39::getCdtrAgtAcct, context);
    }

    @Named("ordinante")
    default AttoreIdentificato ordinante(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return AttoreIdentificatoMapper.INSTANCE.map(getCreditTransfer(pacs), CreditTransferTransaction39::getDbtr, CreditTransferTransaction39::getDbtrAcct);
    }

    @Named("soggettoIstruttore")
    default AttoreIdentificato soggettoIstruttore(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return AttoreIdentificatoMapper.INSTANCE.map(getCreditTransfer(pacs), CreditTransferTransaction39::getInitgPty, null);
    }

    @Named("ordinanteEffettivo")
    default AttoreIdentificato ordinanteEffettivo(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return AttoreIdentificatoMapper.INSTANCE.map(getCreditTransfer(pacs), CreditTransferTransaction39::getUltmtDbtr, null);
    }

    @Named("bancaRegolanteMittente")
    default Intermediario bancaRegolanteMittente(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return IntermediarioMapper.INSTANCE.map(getCreditTransfer(pacs), CreditTransferTransaction39::getInstgAgt, null, context);
    }

    @Named("bancaDellOrdinante")
    default Intermediario bancaDellOrdinante(MxPacs00800108 pacs, @Context AccreditoMappingContext context) {
        return IntermediarioMapper.INSTANCE.map(getCreditTransfer(pacs), CreditTransferTransaction39::getDbtrAgt, CreditTransferTransaction39::getDbtrAgtAcct, context);
    }
}
