package com.flowpay.ccp.credit.transfer.cross.border.controllers;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.AccreditiInquiryMapper;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.inquiries.*;
import com.flowpay.ccp.credit.transfer.cross.border.services.CreditTransferService;
import com.flowpay.ccp.pagination.dto.PaginatedResponse;
import com.flowpay.ccp.pagination.dto.PaginationRequest;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/bonifici")
@Tag(
        name = "Ricerche",
        description = "Ricerche sui bonifici"
)
@Authenticated
public class CreditTransferInquiriesController {

    CreditTransferService creditTransferService;
    PgPool pool;

    CreditTransferInquiriesController(
            CreditTransferService creditTransferService,
            PgPool pool
    ) {
        this.creditTransferService = creditTransferService;
        this.pool = pool;
    }

    @GET
    @Path("/DA_AUTORIZZARE")
    public Uni<PaginatedResponse<BonificoDaAutorizzare>> ricercaBonificiDaAutorizzare(
            @BeanParam ParametriRicercaAutorizzazioneBonifico parametriRicercaAutorizzazioneBonifico,
            @BeanParam PaginationRequest paginationRequest,
            @Context SecurityIdentity identity
    ) {
        return creditTransferService.ricercaBonificiDaAutorizzare(
                parametriRicercaAutorizzazioneBonifico,
                paginationRequest,
                identity
        ).map(page -> BonificoDaAutorizzare.toPaginatedResponse(page, paginationRequest));
    }

    @GET
    @Path("/uscita")
    public Uni<PaginatedResponse<BonificoInUscita>> ricercaBonificiInUscita(
            @BeanParam ParametriRicercaBonificiInUscita parametri,
            @BeanParam PaginationRequest paginationRequest,
            @Context SecurityIdentity identity
    ) {
        return creditTransferService.ricercaBonificiInUscita(
                parametri,
                paginationRequest,
                identity
        ).map(page -> BonificoInUscita.toPaginatedResponse(page, paginationRequest));
    }

    @GET
    @Path("/ingresso/banca")
    public Uni<PaginatedResponse<BonificoInIngressoResult>> ricercaBonificiInIngressoBanca(
            @BeanParam ParametriRicercaBonificiInIngressoBanca parametri,
            @BeanParam PaginationRequest paginationRequest,
            @Context SecurityIdentity identity
    ) {
        return creditTransferService.ricercaBonificiInIngresso(
                parametri,
                paginationRequest,
                identity
        ).map(page -> AccreditiInquiryMapper.INSTANCE.paginatedResponse(page, paginationRequest));
    }


    @GET
    @Path("/ingresso/clientela")
    public Uni<PaginatedResponse<BonificoInIngressoResult>> ricercaBonificiInIngressoClientela(
            @BeanParam ParametriRicercaBonificiInIngressoClientela parametri,
            @BeanParam PaginationRequest paginationRequest,
            @Context SecurityIdentity identity
    ) {
        return creditTransferService.ricercaBonificiInIngresso(
                parametri,
                paginationRequest,
                identity
        ).map(page -> AccreditiInquiryMapper.INSTANCE.paginatedResponse(page, paginationRequest));
    }
}
