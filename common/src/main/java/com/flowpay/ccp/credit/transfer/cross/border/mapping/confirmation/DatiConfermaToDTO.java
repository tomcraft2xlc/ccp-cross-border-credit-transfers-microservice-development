package com.flowpay.ccp.credit.transfer.cross.border.mapping.confirmation;

import com.flowpay.ccp.credit.transfer.cross.border.dto.confirmation.ConfermaRisultatiRisposta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.confirmation.ConfermaRisultatiStepFinaleRisposta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.confirmation.RisultatiConfermaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.dto.verify.*;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingCommonConfig.class)
public interface DatiConfermaToDTO {

    @Mapping(target = "saldo", source = ".")
    @Mapping(target = "avvertenze", source = ".")
    @Mapping(target = "embargo", source = ".")
    @Mapping(target = "cambio", source = ".")
    @Mapping(target = "festivitaPaese", source = "entity.statoConfermaHolidayTablePaese")
    @Mapping(target = "festivitaDivisa", source = "entity.statoConfermaHolidayTableDivisa")
    @Mapping(target = "erroriTecnici", source = "erroriTecnici")
    ConfermaRisultatiRisposta mapRisutaltiStepIniziale(DatiConfermaBonifico.WithLinkedEntities entity);

    @Mapping(target = "confermaBonifico", source = ".")
    @Mapping(target = "erroriTecnici", source = "erroriTecnici")
    ConfermaRisultatiStepFinaleRisposta mapStepFinale(DatiConfermaBonifico.WithLinkedEntities entity);

    @Mapping(target = "statoSaldoRapporto", source = "entity.statoConfermaSaldoRapporto")
    @Mapping(target = "importoSconfinamento", source = "entity.importoSconfinamento")
    RisultatiSaldo mapSaldo(DatiConfermaBonifico.WithLinkedEntities entity);

    default RisultatiSaldo.StatoSaldoRapporto statoSaldo(StatoConfermaSaldoRapporto stato) {
        return switch (stato) {
            case ATTENDE_RISPOSTA -> null;
            case ERRORE -> null;
            case NECESSITA_FORZATURA_SCONFINAMENTO -> RisultatiSaldo.StatoSaldoRapporto.FORZATURA_NECESSARIA;
            case CONFERMATO, CONFERMATO_STEP_VERIFICA -> RisultatiSaldo.StatoSaldoRapporto.SALDO_DISPONIBILE;
            case FALLITO -> RisultatiSaldo.StatoSaldoRapporto.SALDO_NON_DISPONIBILE;
        };
    }

    @Mapping(target = "stato", source = ".")
    @Mapping(target = "avvertenze", source = "entity.avvertenze")
    RisultatiAvvertenze mapAvvertenze(DatiConfermaBonifico.WithLinkedEntities entity);

    @Mapping(target = ".", source = "entity")
    RisultatiAvvertenze.DettaglioAvvertenza mapDettaglioAvvertenza(DatiConfermaBonificoAvvertenza.WithLinkedEntities avvertenze);

    default RisultatiAvvertenze.StatoAvvertenze statoAvvertenze(DatiConfermaBonifico.WithLinkedEntities entity) {
        return switch (entity.getEntity().statoConfermaAvvertenzeRapporto()) {
            case ATTENDE_RISPOSTA -> null;
            case ERRORE -> null;
            case CONFERMATO -> {
                if (entity.avvertenze.isEmpty()) {
                    yield RisultatiAvvertenze.StatoAvvertenze.NESSUNA_AVVERTENZA;
                } else {
                    yield RisultatiAvvertenze.StatoAvvertenze.AVVERTENZE_PRESENTI;
                }
            }
            case CONFERMATO_STEP_VERIFICA -> RisultatiAvvertenze.StatoAvvertenze.NESSUNA_AVVERTENZA;
            case BLOCCO_DARE -> {
                if (entity.avvertenze.stream().anyMatch(element -> element.getEntity().codice().equals("BLOCCODARE"))) {
                    yield RisultatiAvvertenze.StatoAvvertenze.BLOCCO_DARE_CON_AVVERTENZA;
                } else {
                    yield RisultatiAvvertenze.StatoAvvertenze.BLOCCO_DARE;
                }
            }
            case BLOCCO_TOTALE -> RisultatiAvvertenze.StatoAvvertenze.BLOCCO_TOTALE;
            case FALLITO -> RisultatiAvvertenze.StatoAvvertenze.BLOCCO_TOTALE;
        };
    }

    @Mapping(target = "stato", source = "entity.statoConfermaEmbargo")
    RisultatiEmbargo mapEmbargo(DatiConfermaBonifico.WithLinkedEntities entity);

    default RisultatiEmbargo.StatoEmbargo statoEmbargo(StatoConfermaEmbargo stato) {
        return switch (stato) {
            case ATTENDE_RISPOSTA -> null;
            case ERRORE -> null;
            case CONFERMATO, CONFERMATO_STEP_VERIFICA -> RisultatiEmbargo.StatoEmbargo.NON_SOTTO_EMBARGO;
            case NECESSITA_FORZATURA_EMBARGO_PARZIALE -> RisultatiEmbargo.StatoEmbargo.SOTTO_EMBARGO_PARZIALE;
            case FALLITO -> RisultatiEmbargo.StatoEmbargo.SOTTO_EMBARGO_TOTALE;
        };
    }

    @Mapping(target = "cambioValido", source = "entity.statoConfermaCambio")
    RisultatiCambio mapCambio(DatiConfermaBonifico.WithLinkedEntities entity);

    default Boolean statoCambio(StatoConfermaCambio stato) {
        return switch (stato) {
            case ATTENDE_RISPOSTA -> null;
            case ERRORE -> null;
            case NECESSITA_MODIFICA_CAMBIO -> false;
            case CONFERMATO -> true;
        };
    }

    default RisultatiHolidayTable mapHolidayTable(StatoConfermaGenerico stato) {

        if (stato == null) {
            return null;
        }

        if (StatoConfermaGenerico.ATTENDE_RISPOSTA.equals(stato)) {
            return null;
        }


        return new RisultatiHolidayTable(!stato.isConfirmed());
    }

    @Mapping(target = "confermato", source = "entity.statoConfermaBonifico")
    RisultatiConfermaBonifico mapVerificaBonifico(DatiConfermaBonifico.WithLinkedEntities entity);

    default Boolean statoVerificaBonifico(StatoConfermaBonifico stato) {
        return switch (stato) {
            case ATTENDE_RISPOSTA -> null;
            case ERRORE -> null;
            case FALLITO -> false;
            case CONFERMATO -> true;
            case NON_INVIATA -> null;
        };
    }

    @Mapping(target = ".", source = "entity")
    ErroreTecnico mapErroreTecnico(DatiConfermaBonificoErroreTecnico.WithLinkedEntities entity);
}
