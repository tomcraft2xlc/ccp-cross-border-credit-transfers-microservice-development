package com.flowpay.ccp.credit.transfer.cross.border.controllers;

import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.accrediti.Accredito;
import com.flowpay.ccp.credit.transfer.cross.border.services.AccreditiService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

import java.util.UUID;

@Path("/extra-sepa/accredito")
@Authenticated
public class AccreditiController {

    final AccreditiService accreditiService;

    public AccreditiController(AccreditiService accreditiService) {
        this.accreditiService = accreditiService;
    }

    @GET
    @Path("/cliente/{id}")
    public Uni<Accredito> getAccredito(
            UUID id,
            @Context SecurityIdentity identity
    ) {
        return accreditiService.getAccredito(id, identity);
    }

}
