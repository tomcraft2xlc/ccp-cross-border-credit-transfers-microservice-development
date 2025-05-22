package com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.account;

import java.util.UUID;
import java.util.regex.Pattern;

import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.Rapporto;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToBareEntity;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.MappingCommonConfig.DtoToEntityWithLinkedEntitiesMainDocument;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.account.InformazioniRapporto;

@Mapper(config = MappingCommonConfig.class, imports = { UUID.class })
public abstract class InformazioniRapportoMapper {
    @Mapping(target = "id", expression = "java( UUID.randomUUID() )")
    @Mapping(target = "iban", source = "rapporto.identificativo", conditionQualifiedByName = "rapportoIsIban")
    @Mapping(target = "altroID", source = "rapporto.identificativo", conditionQualifiedByName = "rapportoIsAltroId")
    @Mapping(target = "tipoRapporto", source = "tipo")
    @DtoToBareEntity
    abstract InformazioniRapporto bareFromDto(Rapporto rapporto);

    @DtoToEntityWithLinkedEntitiesMainDocument
    public InformazioniRapporto.WithLinkedEntities fromDto(Rapporto rapporto) {
        var bare = bareFromDto(rapporto);
        if (bare == null) {
            return null;
        }
        return bare.withLinkedEntities();
    }

    @Mapping(target = ".", source = "entity")
    @Mapping(target = "identificativo", source = "entity", qualifiedByName = "extractRapporto")
    @Mapping(target = "tipo", source = "entity.tipoRapporto")
    public abstract Rapporto toDto(InformazioniRapporto.WithLinkedEntities informazioniRapporto);

    @Condition
    @Named("rapportoIsIban")
    protected boolean rapportoIsIban(String numeroRapporto) {
        return looksLikeIban(numeroRapporto);
    }

    @Condition
    @Named("rapportoIsAltroId")
    protected boolean rapportoIsAltroId(String numeroRapporto) {
        return !rapportoIsIban(numeroRapporto);
    }

    @Named("extractRapporto")
    protected String extractRapporto(InformazioniRapporto informazioniRapporto) {
        return informazioniRapporto.iban() != null ? informazioniRapporto.iban() : informazioniRapporto.altroID();
    }

    /// Regex usata per controllare se un numero di rapporto è un iban
    private static final Pattern IBAN_CHECKER = Pattern.compile("^[A-Z]{2}.*");

    /**
     * Controlla se il numero di rapporto ha un formato simile a IBAN.
     * 
     * Per ora implementato solo con il controllo dei primi due caratteri.
     * Questo metodo non è stato inserito in {@link Utils} perchè rischierebbe
     * di essere usato in altri parti del codice, e non è abbastanza generale
     * per eseguire ciò che promette. Daltronde controllare completamente
     * se matcha la regex IBAN sarebbe overkill.
     * 
     * @param numeroRapporto Il numero di rapporto da controllare.
     * @return true se il numero di rapporto ha un formato simile a IBAN, false
     *         altrimenti.
     */
    private static boolean looksLikeIban(String numeroRapporto) {
        return IBAN_CHECKER.matcher(numeroRapporto).matches();
    }
}
