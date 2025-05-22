package com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.related_remittance_information;

import com.flowpay.ccp.credit.transfer.cross.border.dto.amount.Importo;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.Indirizzo;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.PrioritaTransazione;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.related_remittance_information.TipoDiRegulatoryReporting;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RiferimentiAggiuntivi(
    String altroIdentificativoPagamento,
    @Valid
    DettaglioRiferimentiAggiuntivi riferimentiAggiuntivi1,
    @Valid
    DettaglioRiferimentiAggiuntivi riferimentiAggiuntivi2,
    String istruzioniBancaRicevente1,
    String istruzioniBancaRicevente2,
    String istruzioniBancaRicevente3,
    String istruzioniBancaRicevente4,
    String istruzioniBancaRicevente5,
    String istruzioniBancaRicevente6,
    @Valid CodiceEIstruzione istruzioneBancaDelBeneficiario1,
    @Valid CodiceEIstruzione istruzioneBancaDelBeneficiario2,

    PrioritaTransazione prioritaTransazione,
    String codiceLivelloDiServizio,
    String dettaglioLivelloDiServizio,
    String classificazionePagamento,
    String dettaglioClassificazionePagamento,
    String codiceTransazione,
    String dettaglioIdentificativoTransazione,
    String codiceTipoServizio,
    String dettaglioTipoServizio,
    BigDecimal valoreCambioIstruito,

    @Valid List<@Valid InformazioniRegulatoryReporting> regulatoryReporting
) {

    public record DettaglioRiferimentiAggiuntivi(
            String modalitaAvvisoPagamento,
            String emailDestinatarioReporting,
            String intestazioneDestinatarioReporting,
            @Valid Indirizzo indirizzoPostale
    ) {

    }

    public record CodiceEIstruzione(
            String codice,
            String istruzioni
    ) { }

    public record InformazioniRegulatoryReporting(
            TipoDiRegulatoryReporting tipo,
            String autoritaRichiedente,
            @Size(max = 2) String paeseAutoritaRichiedente,
            @Valid List<@Valid DettagliRegulatoryReporting> dettagliRegulatoryReporting
    ) {
        public record DettagliRegulatoryReporting(
                String dettaglio,
                LocalDate data,
                @Valid Importo importo,
                @Size(max = 2) String paese,
                String informazioniAggiuntive
        ) {

        }
    }
}
