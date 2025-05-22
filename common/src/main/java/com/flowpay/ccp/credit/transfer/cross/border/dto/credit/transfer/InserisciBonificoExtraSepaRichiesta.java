package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.details.DettagliBonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.AltriIntermediari;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.intermediary.Intermediario;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.kind.SottoTipologiaBonificoRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.related_remittance_information.RiferimentiAggiuntivi;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.remittance.DettagliCausale;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.sistema_di_regolamento.InfoSistemaDiRegolamento;


/**
 * Metodi comuni ad entrambi i tipi di bonifico
 */
public interface InserisciBonificoExtraSepaRichiesta {

        String tid();
        default String getTid() { return tid(); }

        SottoTipologiaBonificoRichiesta sottoTipologiaBonifico();
        default SottoTipologiaBonificoRichiesta getSottoTipologiaBonifico() { return sottoTipologiaBonifico(); }

        Intermediario bancaOrdinante();
        default Intermediario getBancaOrdinante() { return bancaOrdinante(); }

        Intermediario bancaDestinataria();
        default Intermediario getBancaDestinataria() { return bancaDestinataria(); }

        Intermediario bancaDelBeneficiario();
        default Intermediario getBancaDelBeneficiario() { return bancaDelBeneficiario(); }

        DettagliBonificoExtraSepa dettagliBonifico();
        default DettagliBonificoExtraSepa getDettagliBonifico() { return dettagliBonifico(); }

        InfoSistemaDiRegolamento sistemaDiRegolamento();
        default InfoSistemaDiRegolamento getSistemaDiRegolamento() { return sistemaDiRegolamento(); }

        RiferimentiAggiuntivi riferimentiAggiuntivi();
        default RiferimentiAggiuntivi getRiferimentiAggiuntivi() { return riferimentiAggiuntivi(); }

        AltriIntermediari altriIntermediari();
        default AltriIntermediari getAltriIntermediari() { return altriIntermediari(); }

        DettagliCausale dettagliCausale();
        default DettagliCausale getDettagliCausale() { return dettagliCausale(); }

        String user();
        default String getUser() { return user(); }

        boolean isBancaABanca();

        DettagliDocumentoDiCopertura documentoDiCopertura();
        default DettagliDocumentoDiCopertura getDocumentoDiCopertura() { return documentoDiCopertura(); }
}

