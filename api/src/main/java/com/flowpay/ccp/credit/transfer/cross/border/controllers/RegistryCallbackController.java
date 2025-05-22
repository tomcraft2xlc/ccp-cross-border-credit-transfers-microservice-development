package com.flowpay.ccp.credit.transfer.cross.border.controllers;


import com.flowpay.ccp.credit.transfer.cross.border.Constants;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.datechange.RestartHandleDateChangePayload;
import com.flowpay.ccp.job.JobData;
import com.flowpay.ccp.job.JobPublisher;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Named;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import java.util.UUID;

@Path("/registry/results")
@Authenticated
public class RegistryCallbackController {

    JobPublisher jobPublisher;

    RegistryCallbackController(@Named(Constants.BEAN_JOB_PUBLISHER_INTERNAL) JobPublisher jobPublisher) {
        this.jobPublisher = jobPublisher;
    }

    @POST
    @Path("date-change/{processID}/{creditTransferID}/{idAutorizzazione}/{proposedDate}")
    public Uni<Void> handleChangeDateResult(
            @PathParam("processID") UUID processID,
            @PathParam("creditTransferID") UUID creditTransferID,
            @PathParam("idAutorizzazione") UUID idAutorizzazione,
            @PathParam("proposedDate") Long proposedDate
            ) {
        return jobPublisher.scheduleJob(
                new JobData(
                        UUID.randomUUID(),
                        Constants.JOB_RESTART_HANDLE_DATE_CHANGE,
                        new RestartHandleDateChangePayload(
                                creditTransferID,
                                idAutorizzazione,
                                proposedDate,
                                processID,
                                null,
                                null,
                                true
                        )
                )
        );
    }

}
