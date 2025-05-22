package com.flowpay.ccp.credit.transfer.cross.border.controllers;

import com.flowpay.ccp.credit.transfer.cross.border.dto.authorization.InserisciAutorizzazioneBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.dto.authorization.ListaAutorizzazioniBonifico;
import com.flowpay.ccp.credit.transfer.cross.border.mapping.authorization.ListaAutorizzazioniBonificoMapper;
import com.flowpay.ccp.credit.transfer.cross.border.services.AuthorizationService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/{id}/autorizzazioni")
@Authenticated
public class AuthorizationController {

    private final AuthorizationService authorizationService;
    private final ListaAutorizzazioniBonificoMapper mapper;

    AuthorizationController(
            AuthorizationService authorizationService,
            ListaAutorizzazioniBonificoMapper mapper
    ) {
        this.authorizationService = authorizationService;
        this.mapper = mapper;
    }

    @POST
    @Operation(
        summary = "Crea una risorsa di autorizzazione per un bonifico",
        description = "Crea la risorsa di autorizzazione per un bonifico. Gestisce il ciclo di vita del cruscotto autorizzativo"
    )
    @Parameter(
        name = "id",
        in = ParameterIn.PATH,
        description = "L'id del bonifico da modificare",
        schema = @Schema( implementation = UUID.class )
    )
    @APIResponse(
        responseCode = "201",
        description = "Autorizzazione creata e accettata con successo",
        content = {
                @Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ListaAutorizzazioniBonifico.class)
                )
        }
    )
    public Uni<RestResponse<ListaAutorizzazioniBonifico>> insertCreditTransferAuthorization(
            UUID id,
            @Valid InserisciAutorizzazioneBonifico request,
            @Context SecurityIdentity identity) {
        return authorizationService.create(id, request, identity)
                .map(response -> RestResponse.status(Status.CREATED, response));
    }
    @GET
    @Operation(
        summary = "Legge tutte le autorizzazioni date a un bonifico",
        description = "Legge tutte le risorse di autorizzazione per un bonifico."
    )
    @Parameter(
        name = "id",
        in = ParameterIn.PATH,
        description = "L'id del bonifico da modificare",
        schema = @Schema( implementation = UUID.class )
    )
    public Uni<ListaAutorizzazioniBonifico> getCreditTransferAuthorizations(
            UUID id,
            @Context SecurityIdentity identity
    ) {
        return authorizationService.get(id, identity);
    }

    @POST
    @Path("/cambia-data")
    @Operation(
        summary = "Cambia la data di un bonifico al successivo giorno lavorativo",
        description = """
                Cambia la data di un bonifico che sta venendo gestito oltre l'orario di cutoff al 
                primo giorno lavorativo (rispetto a quando viene eseguia la chiamata).
                """
    )
    @Parameter(
        name = "id",
        in = ParameterIn.PATH,
        description = "L'id del bonifico da modificare",
        schema = @Schema( implementation = UUID.class )
    )
    public Uni<ListaAutorizzazioniBonifico> changeInterbankSettlementDate(
            @PathParam("id") UUID id,
            @Context SecurityIdentity identity
    ) {
        return authorizationService.cambiaData(id, identity);
    }

}
