package com.flowpay.ccp.credit.transfer.cross.border.controllers;

import com.flowpay.ccp.auth.client.AuthConstants;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.kind.WireTransferType;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.kind.SottotipologieBonificoRisposta;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.CreditTransferKindMapper;
import com.flowpay.ccp.credit.transfer.cross.border.services.CreditTransferKindService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Path("/sotto-tipologie-bonifico")
@Authenticated
@SuppressWarnings("unused")
@Tag(name = "Info")
public class CreditTransferKindController {

    private static final Logger LOG = Logger.getLogger(CreditTransferKindController.class);

    CreditTransferKindService service;
    CreditTransferKindMapper mapper;

    CreditTransferKindController(
            CreditTransferKindService service,
            CreditTransferKindMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GET
    @Path("/{tipo}")
    @Operation(summary = "Ottieni i sottotipi disponibili", description = "Ottieni tutti i sottotipi di bonifico disponibili per il dato tipo di bonifico")
    @Parameter(name = "tipo", description = "Il tipo di bonifico del quale si vogliono sapere i sottotipi")
    public Uni<SottotipologieBonificoRisposta> getWireTransferKinds(
            @PathParam("tipo") WireTransferType type,
            @Context SecurityIdentity identity) {
        String channel = identity.getAttribute(AuthConstants.CHANNEL_ATTRIBUTE);
        LOG.info("Getting wire transfer kinds for channel " + channel + " and type " + type);
        return this.service.list(channel, type, identity)
                .collect().asList()
                .map(mapper::mapDTO);
    }
}
