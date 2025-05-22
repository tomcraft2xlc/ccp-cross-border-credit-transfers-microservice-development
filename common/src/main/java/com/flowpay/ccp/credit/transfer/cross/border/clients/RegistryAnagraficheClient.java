package com.flowpay.ccp.credit.transfer.cross.border.clients;

import com.flowpay.ccp.pagination.dto.PaginatedResponse;
import com.flowpay.ccp.pagination.dto.PaginationRequest;
import com.flowpay.ccp.registry.RicercaDivisaTipoRichiesta;
import com.flowpay.ccp.registry.VerificaCambioTipoRichiesta;
import com.flowpay.ccp.registry.VerificaHolidayTableTipoCodice;
import com.flowpay.ccp.registry.VerificaHolidayTableTipoRichiesta;
import com.flowpay.ccp.registry.dto.responses.DettaglioAccountResponse;
import com.flowpay.ccp.registry.dto.responses.RicercaBicResponse;
import com.flowpay.ccp.registry.dto.responses.RicercaDivisaResponse;
import com.flowpay.ccp.registry.dto.responses.VerificaCambioResponse;
import com.flowpay.ccp.registry.dto.responses.VerificaHolidayTableResponse;

import com.flowpay.ccp.resources.poll.client.PollConstants;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;

@Path("/anagrafiche")
public interface RegistryAnagraficheClient {

    @GET
    @Path("/accounts/{accountID}")
    public Uni<DettaglioAccountResponse> dettaglioAccount(
            @PathParam("accountID") String accountID
    );


    @GET
    @Path("/currencies/{type}")
    public Uni<PaginatedResponse<RicercaDivisaResponse>> ricercaDivisa(
            @BeanParam PaginationRequest paginationRequest,
            @PathParam("type") RicercaDivisaTipoRichiesta tipoRichiesta,
            @QueryParam("codiceDivisaIso") String codiceDivisaIso,
            @QueryParam("descrizioneDivisa") String descrizioneDivisa,
            @QueryParam("forced") Boolean forced
    );

    @GET
    @Path("/holidays/{type}")
    public Uni<PaginatedResponse<VerificaHolidayTableResponse>> verificaHolidayTable(
            @BeanParam PaginationRequest paginationRequest,
            @PathParam("type") VerificaHolidayTableTipoRichiesta tipoRichiesta,
            @QueryParam("tipoCodice") VerificaHolidayTableTipoCodice tipoCodice,
            @QueryParam("codiceIso") String codiceIso,
            @QueryParam("dataRichiesta") Long dataRichiesta,
            @QueryParam("forced") Boolean forced
    );

    @GET
    @Path("/verifica-cambio/{type}")
    public Uni<VerificaCambioResponse> verificaCambio(
            @PathParam("type") VerificaCambioTipoRichiesta tipoRichiesta,
            @QueryParam("codiceIsoDivisa") String codiceIsoDivisa,
            @QueryParam("valoreCambio") String valoreCambio,
            @QueryParam("forced") Boolean forced
    );

    @GET
    @Path("/bic")
    public Uni<PaginatedResponse<RicercaBicResponse>> ricercaBic(
            @BeanParam PaginationRequest paginationRequest,
            @QueryParam("codiceBic") String codiceBic,
            @QueryParam("descrizioneBic") String descrizioneBic,
            @QueryParam("abi") Long abi,
            @QueryParam("forced") Boolean forced
            ) ;

    @GET
    @Path("/bic/{bic}")
    public Uni<RicercaBicResponse> dettaglioBanca(
            @PathParam("bic") String bic,
            @HeaderParam(PollConstants.POLLABLE_CALLBACK_HEADER) String callback,
            @QueryParam("forced") Boolean forced
    );

    @GET
    @Path("/bic/{bic}")
    public Uni<RicercaBicResponse> dettaglioBanca(
            @PathParam("bic") String bic,
            @QueryParam("forced") Boolean forced
    );
}
