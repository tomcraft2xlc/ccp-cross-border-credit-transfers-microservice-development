package com.flowpay.ccp.credit.transfer.cross.border.services;

import com.flowpay.ccp.credit.transfer.cross.border.FieldValidator;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.InserisciBonificoExtraSepaRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind.SottoTipologiaBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind.WireTransferType;
import com.flowpay.ccp.persistence.DataSources;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import org.javatuples.Pair;
import org.jboss.logging.Logger;

@RequestScoped
public class CreditTransferKindService {

    DataSources dataSources;

    private static final Logger logger = Logger.getLogger(CreditTransferKindService.class);

    public CreditTransferKindService(DataSources dataSources) {
        this.dataSources = dataSources;
    }

    public Multi<SottoTipologiaBonifico> list(String channelID, WireTransferType type, SecurityIdentity identity) {
        var entity = new SottoTipologiaBonifico.Entity();
        var repository = this.dataSources.dataSource(identity);
        return entity.repository(repository).list(channelID, type);
    }

    public enum Result {
        /// Il bonifico è permesso
        ALLOWED,
        /// Il bonifico non è permesso in questo canale
        NOT_ALLOWED,
        /// Il bonifico è permesso, ma alcuni campi mancano dal DTO
        MISSING_FIELDS,
        /// Un bonifico cliente a cliente è stato mandato come banca a banca o viceversa
        WRONG_TARGETS
    }


    /**
     * Verifica se il bonifico extra-SEPA può essere inserito sul canale specificato.
     * <p>
     * La verifica prevede due fasi:
     * <ol>
     * <li>Verifica se il tipo di bonifico specificato è disponibile sul canale specificato.
     * <li>Verifica se i campi richiesti per il tipo di bonifico sono presenti nel DTO.
     * <li>Verifica se il tipo di bonifico richiesto corrisponde al tipo di bonifico del canale
     * </ol>
     * <p>
     * In caso di esito positivo, restituisce l'oggetto {@code SottoTipologiaBonifico} con l'informazione
     * relativa al tipo di bonifico disponibile sul canale.
     * <p>
     * In caso di esito negativo, restituisce l'oggetto {@code Result} con la ragione dell'errore.
     * <p>
     * @param request il DTO contenente le informazioni relative al bonifico da inserire
     * @param channelID l'ID del canale sul quale inserire il bonifico
     * @return un Uni che contiene l'oggetto {@code SottoTipologiaBonifico} o l'oggetto {@code Result}
     *         contenente la ragione dell'errore
     */
    public Uni<Pair<Result, SottoTipologiaBonifico>> validate(InserisciBonificoExtraSepaRichiesta request, String channelID, SecurityIdentity identity) {
        var entity = new SottoTipologiaBonifico.Entity();
        var repository = this.dataSources.dataSource(identity);
        return entity.repository(repository).isAllowed(channelID, request.sottoTipologiaBonifico().tipo(), request.user() != null)
        .map(result -> {
            var creditTransferKind = result.orElseThrow();
            if (creditTransferKind.campiDTOObbligatori() != null && !new FieldValidator(creditTransferKind.campiDTOObbligatori()).isValid(request)) {
                    return Pair.with(Result.MISSING_FIELDS, (SottoTipologiaBonifico)null);
            }
            if (!creditTransferKind.bancaABanca().equals(request.isBancaABanca())) {
                return Pair.with(Result.WRONG_TARGETS, (SottoTipologiaBonifico)null);
            }
            return Pair.with(Result.ALLOWED, creditTransferKind);
        }).onFailure()
        .recoverWithItem(Pair.with(Result.NOT_ALLOWED, null));
    }

}
