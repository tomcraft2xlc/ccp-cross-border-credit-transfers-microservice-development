package com.flowpay.ccp.credit.transfer.cross.border.controllers;


import com.flowpay.ccp.business.log.handler.process.Process;
import com.flowpay.ccp.business.log.handler.process.ProcessInfo;
import com.flowpay.ccp.credit.transfer.cross.border.dto.confirmation.ConfermaRisultatiRisposta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.confirmation.ConfermaRisultatiStepFinaleRisposta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.BonificoExtraSepaRisposta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.InserisciBonificoExtraSepaBancaRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.InserisciBonificoExtraSepaClienteRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit_transfer.RichiestaModificaBonificoStato;
import com.flowpay.ccp.credit.transfer.cross.border.dto.verify.VerificaRisultatiRisposta;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.confirmation.DatiConfermaToDTO;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.credit.transfer.BonificoExtraSepaMapper;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.verify.DatiVerificaToDTO;
import com.flowpay.ccp.credit.transfer.cross.border.services.CreditTransferKindService;
import com.flowpay.ccp.credit.transfer.cross.border.services.CreditTransferService;
import com.flowpay.ccp.persistence.DataSources;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;

import java.util.UUID;

@Path("/bonifico")
@SuppressWarnings("unused")
@Authenticated
@Tag(
        name = "Bonifici",
        description = "Gestisci un bonifico"
)
public class CreditTransferController {

    final CreditTransferKindService creditTransferKindService;

    final CreditTransferService creditTransferService;

    final BonificoExtraSepaMapper bonificoExtraSepaMapper;

    final DatiVerificaToDTO datiVerificaMapper;
    final DatiConfermaToDTO datiConfermaMapper;

    final DataSources dataSources;

    CreditTransferController(
            CreditTransferKindService creditTransferKindService,
            CreditTransferService creditTransferService,
            DataSources dataSources,
            BonificoExtraSepaMapper bonificoExtraSepaMapper,
            DatiVerificaToDTO datiVerificaMapper,
            DatiConfermaToDTO datiConfermaMapper) {
        this.creditTransferKindService = creditTransferKindService;
        this.creditTransferService = creditTransferService;
        this.dataSources = dataSources;
        this.bonificoExtraSepaMapper = bonificoExtraSepaMapper;
        this.datiVerificaMapper = datiVerificaMapper;
        this.datiConfermaMapper = datiConfermaMapper;
    }

    @GET
    @Path("/{id}")
    @Operation(
            summary = "Ottiene lo stato di un bonifico",
            description = """
                    Ottiene lo stato attuale di un bonifico.
                    """
    )
    @Parameter(
            name = "id",
            in = ParameterIn.PATH,
            description = "L'id del bonifico",
            schema = @Schema(implementation = UUID.class)
    )
    public Uni<BonificoExtraSepaRisposta> getCreditTransfer(
            UUID id,
            @Context SecurityIdentity identity) {
        return creditTransferService.getCreditTransfer(id, identity);
    }

    @GET
    @Path("/{id}/risultati-verifica")
    public Uni<RestResponse<VerificaRisultatiRisposta>> getRisultatiVerifica(
            UUID id,
            @Context SecurityIdentity identity
    ) {
        return creditTransferService.getRisultatiVerifica(id, identity)
        .map(datiVerificaMapper::map)
        .map(RestResponse::ok)
        .onFailure().recoverWithItem(RestResponse.status(Status.NO_CONTENT));
    }

    @GET
    @Path("/{id}/risultati-conferma")
    public Uni<RestResponse<ConfermaRisultatiRisposta>> getRisultatiConferma(
            UUID id,
            @Context SecurityIdentity identity
    ) {
        return creditTransferService.getRisultatiConferma(id, identity)
        .map(datiConfermaMapper::mapRisutaltiStepIniziale)
        .map(RestResponse::ok)
        .onFailure().recoverWithItem(RestResponse.status(Status.NO_CONTENT));
    }

    @GET
    @Path("/{id}/risultati-conferma/step-finale")
    public Uni<RestResponse<ConfermaRisultatiStepFinaleRisposta>> getRisultatiConfermaStepFinale(
            UUID id,
            @Context SecurityIdentity identity
    ) {
        return creditTransferService.getRisultatiConferma(id, identity)
        .map(datiConferma -> {
            if (!datiConferma.getEntity().step2CallEnded()) {
                throw new NotFoundException("dati conferma step finale per bonifico %s non ancora pronti".formatted(id));
            }
            return datiConferma;
        })
        .map(datiConfermaMapper::mapStepFinale)
        .map(RestResponse::ok)
        .onFailure().recoverWithItem(RestResponse.status(Status.NO_CONTENT));
    }

    @PATCH
    @Path("/{id}")
    @ProcessInfo("update-extra-sepa-credit-transfer")
    @Operation(
            summary = "Modifica un bonifico",
            description = """
                    Il body della richiesta contiene i campi da aggiornare.
                    
                    La transizione tra il nuovo e precedente stato deve essere permesso dalla macchina a stati dei bonifici.
                    """
    )
    @Parameter(
            name = "id",
            in = ParameterIn.PATH,
            description = "L'id del bonifico da modificare",
            schema = @Schema(implementation = UUID.class)
    )
    @APIResponse(
            responseCode = "200",
            description = "Bonifico modificato con successo.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = BonificoExtraSepaRisposta.class)
                    )
            }
    )
    public Uni<BonificoExtraSepaRisposta> updateCreditTransfer(
            UUID id,
            @Valid RichiestaModificaBonificoStato request,
            @Context SecurityIdentity identity) {
        return creditTransferService
                .updateBonifico(id, identity, request.nuovoStato());
    }

    @POST
    @Path("/cliente")
    @Process
    @ProcessInfo("insert-extra-sepa-credit-transfer")
    @Operation(
            summary = "Crea un nuovo bonifico account a account",
            description = """
                    Il body della richiesta contiene tutti i campi necessari per inserire un nuovo bonifico nel sistema account to account.
                    
                    Il canale dal quale arriva la richiesta invece è riconosciuto dalle informazioni di autenticazione fornite.
                    
                    Il canale di ingresso permette di inserire un bonifico in uno stato diverso, configurabile per banca. è possibile inoltre modificare lo stato iniziale in cui il bonifico viene inserito, lo stato iniziale non puo' essere uno stato più avanzato rispetto allo stato iniziale di default del canale.
                    """
    )
    @APIResponse(
            responseCode = "201",
            description = "Bonifico creato con successo.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = BonificoExtraSepaRisposta.class)
                    )
            }
    )
    public Uni<RestResponse<BonificoExtraSepaRisposta>> insertCreditTransfer(
            @Valid InserisciBonificoExtraSepaClienteRichiesta request,
            @Context SecurityIdentity identity) {
        if (request == null) {
            return Uni.createFrom().item(RestResponse.status(Status.BAD_REQUEST));
        }
        return creditTransferService.insertCreditTransfer(request, identity)
                .map(response -> RestResponse.status(Status.CREATED, response));
    }


    @POST
    @Path("/banca")
    @Process
    @ProcessInfo("insert-extra-sepa-credit-transfer")
    @Operation(
            summary = "Crea un nuovo bonifico banca a banca",
            description = """
                    Il body della richiesta contiene tutti i campi necessari per inserire un nuovo bonifico nel sistema banca a banca.
                    
                    Il canale dal quale arriva la richiesta invece è riconosciuto dalle informazioni di autenticazione fornite.
                    
                    Il canale di ingresso permette di inserire un bonifico in uno stato diverso, configurabile per banca. è possibile inoltre modificare lo stato iniziale in cui il bonifico viene inserito, lo stato iniziale non puo' essere uno stato più avanzato rispetto allo stato iniziale di default del canale.
                    """
    )
    @APIResponse(
            responseCode = "201",
            description = "Bonifico creato con successo.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = BonificoExtraSepaRisposta.class)
                    )
            }
    )
    public Uni<RestResponse<BonificoExtraSepaRisposta>> insertCreditTransferBanca(
            @Valid InserisciBonificoExtraSepaBancaRichiesta request,
            @Context SecurityIdentity identity) {
        if (request == null) {
            return Uni.createFrom().item(RestResponse.status(Status.BAD_REQUEST));
        }
        return creditTransferService.insertCreditTransfer(request, identity)
                .map(response -> RestResponse.status(Status.CREATED, response));
    }

}
