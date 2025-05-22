package com.flowpay.ccp.credit.transfer.cross.border.controllers;



import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.flowpay.ccp.auth.client.CabelForwardedCredential;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.dto.bank.ConfigurazioneBanca;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

@Path("/info-banca")
@Authenticated
@Tag(
    name = "Info",
    description = "Configurazioni e altre info"
)
public class BankInfoController {


    BanksConfig banksConfig;

    BankInfoController(BanksConfig banksConfig) {
        this.banksConfig = banksConfig;
    }

    @GET
    @Operation(
        summary = "Ottieni la configurazione della banca",
        description = """
                Recupera la banca dell'utente dal quale proviene la richiesta,
                e raccoglie le configurazioni relative ad essa.
                """
    )
    @APIResponse(
        responseCode = "200",
        description = "Le configurazioni recuperate della banca",
        content = {
            @Content(mediaType = MediaType.APPLICATION_JSON, schema= @Schema(implementation = ConfigurazioneBanca.class))
        }
    )
    public Uni<ConfigurazioneBanca> getBankInfo(@Context SecurityIdentity identity) {
        var credentials = identity.getCredential(CabelForwardedCredential.class);
        return Uni.createFrom().item(banksConfig.bank().get(credentials.abi()))
                .map(info -> new ConfigurazioneBanca(
                        info.bic(),
                        new ConfigurazioneBanca.InfoCanale(
                                info.channel().t2().attivo(),
                                info.channel().t2().tramitatoDa().orElse(null),
                                info.channel().t2().oraCutOff().orElse(17),
                                info.channel().t2().minutoCutOff().orElse(0)
                        ),
                        new ConfigurazioneBanca.InfoCanale(
                                info.channel().cbpr().attivo(),
                                info.channel().cbpr().tramitatoDa().orElse(null),
                                info.channel().cbpr().oraCutOff().orElse(17),
                                info.channel().cbpr().minutoCutOff().orElse(0)
                        ),
                        info.listinoCommissioni(),
                        info.sconfinamento(),
                        "EUR",
                        "Euro",
                        "EUR - Euro"
                ));

    }
}
