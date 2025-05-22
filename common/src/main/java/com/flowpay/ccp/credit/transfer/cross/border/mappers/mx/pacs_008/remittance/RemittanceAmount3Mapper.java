package com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.pacs_008.remittance;

import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.MxMappingConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.mx.common.amount.ActiveOrHistoricCurrencyAndAmountMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.TipoDettaglioImportoDocumentoDiRiferimento;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document.DettaglioImporto;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.remittance.referred_document.DettaglioLineaDocumentoDiRiferimentoDettaglioImporto;
import com.prowidesoftware.swift.model.mx.dic.ActiveOrHistoricCurrencyAndAmount;
import com.prowidesoftware.swift.model.mx.dic.RemittanceAmount3;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

import java.util.Collection;

@Mapper(config = MxMappingConfig.class)
@DecoratedWith(RemittanceAmount3Mapper.Decorator.class)
public interface RemittanceAmount3Mapper {

    default RemittanceAmount3 map(Collection<DettaglioLineaDocumentoDiRiferimentoDettaglioImporto.WithLinkedEntities> dettagliImporto) {
        var duePyblAmt = dettagliImporto.stream().filter(dettagli -> dettagli.dettaglioImporto.getEntity().tipoDettaglioImportoDocumentoDiRiferimento() == TipoDettaglioImportoDocumentoDiRiferimento.IMPORTO_DOVUTO)
                .findFirst().map(dettagli -> dettagli.dettaglioImporto).map(DettaglioImporto.WithLinkedEntities::getEntity).orElse(null);
        var dscntApldAmt = dettagliImporto.stream()
                .filter(dettagli -> dettagli.dettaglioImporto.getEntity().tipoDettaglioImportoDocumentoDiRiferimento() == TipoDettaglioImportoDocumentoDiRiferimento.SCONTO)
                .map(dettagli -> dettagli.dettaglioImporto).map(DettaglioImporto.WithLinkedEntities::getEntity)
                .toList();
        var cdtNoteAmt = dettagliImporto.stream().filter(dettagli -> dettagli.dettaglioImporto.getEntity().tipoDettaglioImportoDocumentoDiRiferimento() == TipoDettaglioImportoDocumentoDiRiferimento.NOTA_DI_CREDITO)
                .findFirst().map(dettagli -> dettagli.dettaglioImporto).map(DettaglioImporto.WithLinkedEntities::getEntity).orElse(null);
        var taxAmt = dettagliImporto.stream().filter(dettagli -> dettagli.dettaglioImporto.getEntity().tipoDettaglioImportoDocumentoDiRiferimento() == TipoDettaglioImportoDocumentoDiRiferimento.TASSE)
                .map(dettagli -> dettagli.dettaglioImporto).map(DettaglioImporto.WithLinkedEntities::getEntity)
                .toList();
        var adjstmntAmtAndRsn = dettagliImporto.stream().filter(dettagli -> dettagli.dettaglioImporto.getEntity().tipoDettaglioImportoDocumentoDiRiferimento() == TipoDettaglioImportoDocumentoDiRiferimento.RETTIFICA)
                .map(dettagli -> dettagli.dettaglioImporto).map(DettaglioImporto.WithLinkedEntities::getEntity)
                .toList();
        var rmtdAmt = dettagliImporto.stream().filter(dettagli -> dettagli.dettaglioImporto.getEntity().tipoDettaglioImportoDocumentoDiRiferimento() == TipoDettaglioImportoDocumentoDiRiferimento.IMPORTO_DISPOSTO)
                .findFirst()
                .map(dettagli -> dettagli.dettaglioImporto).map(DettaglioImporto.WithLinkedEntities::getEntity)
                .orElse(null);

        var result = new RemittanceAmount3();

        result.setDuePyblAmt(this.amt(duePyblAmt));
        result.getDscntApldAmt().addAll(
                dscntApldAmt.stream().map(DiscountAmountAndType1Mapper.INSTANCE::map).toList()
        );
        result.setCdtNoteAmt(this.amt(cdtNoteAmt));
        result.getTaxAmt().addAll(
                taxAmt.stream().map(TaxAmountAndType1Mapper.INSTANCE::map).toList()
        );
        result.getAdjstmntAmtAndRsn().addAll(
                adjstmntAmtAndRsn.stream().map(DocumentAdjustment1Mapper.INSTANCE::map).toList()
        );
        result.setRmtdAmt(this.amt(rmtdAmt));
        return result;
    }

    default ActiveOrHistoricCurrencyAndAmount amt(DettaglioImporto importo) {
        if (importo != null) {
            return ActiveOrHistoricCurrencyAndAmountMapper.INSTANCE.map(importo.importo(), importo.divisa());
        }
        return null;
    }

    abstract class Decorator implements RemittanceAmount3Mapper {

        private final RemittanceAmount3Mapper delegate;

        Decorator(RemittanceAmount3Mapper delegate) {
            this.delegate = delegate;
        }

        @Override
        public RemittanceAmount3 map(Collection<DettaglioLineaDocumentoDiRiferimentoDettaglioImporto.WithLinkedEntities> dettagliImporto) {
            return Utils.allFieldsEmpty(delegate.map(dettagliImporto));
        }
    }
}
