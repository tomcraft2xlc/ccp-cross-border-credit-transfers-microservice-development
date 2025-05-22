package com.flowpay.ccp.credit.transfer.cross.border.mapping.verify;

import com.flowpay.ccp.credit.transfer.cross.border.dto.verify.*;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingCommonConfig.class)
public interface DatiVerificaToDTO {

    @Mapping(target = "verificaSistemaDiRegolamento", source = ".")
    @Mapping(target = "saldo", source = ".")
    @Mapping(target = "avvertenze", source = ".")
    @Mapping(target = "embargo", source = ".")
    @Mapping(target = "cambio", source = ".")
    @Mapping(target = "festivitaPaese", source = "entity.statoVerificaHolidayTablePaese")
    @Mapping(target = "festivitaDivisa", source = "entity.statoVerificaHolidayTableDivisa")
    @Mapping(target = "verificaBonifico", source = ".")
    @Mapping(target = "erroriTecnici", source = "erroriTecnici")
    VerificaRisultatiRisposta map(DatiVerificaBonifico.WithLinkedEntities entity);


    default VerificaSistemaDiRegolamento mapSistemaDiRegolamento(DatiVerificaBonifico.WithLinkedEntities entity) {
        return new VerificaSistemaDiRegolamento(true, null);
    }

    @Mapping(target = "statoSaldoRapporto", source = "entity.statoVerificaSaldoRapporto")
    @Mapping(target = "importoSconfinamento", source = "entity.importoSconfinamento")
    RisultatiSaldo mapSaldo(DatiVerificaBonifico.WithLinkedEntities entity);

    default RisultatiSaldo.StatoSaldoRapporto statoSaldo(StatoVerificaSaldoRapporto stato) {
        return switch (stato) {
            case ATTENDE_RISPOSTA -> null;
            case ERRORE -> null;
            case NECESSITA_FORZATURA_SCONFINAMENTO -> RisultatiSaldo.StatoSaldoRapporto.FORZATURA_NECESSARIA;
            case VERIFICATO -> RisultatiSaldo.StatoSaldoRapporto.SALDO_DISPONIBILE;
            case FALLITO -> RisultatiSaldo.StatoSaldoRapporto.SALDO_NON_DISPONIBILE;
        };
    }

    @Mapping(target = "stato", source = ".")
    @Mapping(target = "avvertenze", source = "entity.avvertenze")
    RisultatiAvvertenze mapAvvertenze(DatiVerificaBonifico.WithLinkedEntities entity);

    @Mapping(target = ".", source = "entity")
    RisultatiAvvertenze.DettaglioAvvertenza mapDettaglioAvvertenza(DatiVerificaBonificoAvvertenza.WithLinkedEntities avvertenze);

    default RisultatiAvvertenze.StatoAvvertenze statoAvvertenze(DatiVerificaBonifico.WithLinkedEntities entity) {
        return switch (entity.getEntity().statoVerificaAvvertenzeRapporto()) {
            case ATTENDE_RISPOSTA -> null;
            case ERRORE -> null;
            case VERIFICATO -> {
                if (entity.avvertenze.isEmpty()) {
                    yield RisultatiAvvertenze.StatoAvvertenze.NESSUNA_AVVERTENZA;
                } else {
                    yield RisultatiAvvertenze.StatoAvvertenze.AVVERTENZE_PRESENTI;
                }
            }
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

    @Mapping(target = "stato", source = "entity.statoVerificaEmbargo")
    RisultatiEmbargo mapEmbargo(DatiVerificaBonifico.WithLinkedEntities entity);

    default RisultatiEmbargo.StatoEmbargo statoEmbargo(StatoVerificaEmbargo stato) {
        return switch (stato) {
            case ATTENDE_RISPOSTA -> null;
            case ERRORE -> null;
            case VERIFICATO -> RisultatiEmbargo.StatoEmbargo.NON_SOTTO_EMBARGO;
            case NECESSITA_FORZATURA_EMBARGO_PARZIALE -> RisultatiEmbargo.StatoEmbargo.SOTTO_EMBARGO_PARZIALE;
            case FALLITO -> RisultatiEmbargo.StatoEmbargo.SOTTO_EMBARGO_TOTALE;
        };
    }

    @Mapping(target = "cambioValido", source = "entity.statoVerificaCambio")
    RisultatiCambio mapCambio(DatiVerificaBonifico.WithLinkedEntities entity);

    default Boolean statoCambio(StatoVerificaCambio stato) {
        return switch (stato) {
            case ATTENDE_RISPOSTA -> null;
            case ERRORE -> null;
            case NECESSITA_MODIFICA_CAMBIO -> false;
            case VERIFICATO -> true;
        };
    }


    default RisultatiHolidayTable mapHolidayTable(StatoVerificaGenerico stato) {

        if (stato == null) {
            return null;
        }

        if (StatoVerificaGenerico.ATTENDE_RISPOSTA.equals(stato)) {
            return null;
        }


        return new RisultatiHolidayTable(!stato.isVerified());
    }

    @Mapping(target = "richiestoCambiamentoIban", source = "entity.statoVerificaBonifico")
    RisultatiVerificaBonifico mapVerificaBonifico(DatiVerificaBonifico.WithLinkedEntities entity);

    default Boolean statoVerificaBonifico(StatoVerificaBonifico stato) {
        return switch (stato) {
            case ATTENDE_RISPOSTA -> null;
            case ERRORE -> null;
            case VERIFICATO -> false;
            case NECESSITA_MODIFICA_IBAN -> true;
        };
    }

    @Mapping(target = ".", source = "entity")
    ErroreTecnico mapErroreTecnico(DatiVerificaBonificoErroreTecnico.WithLinkedEntities entity);
}
