package com.flowpay.ccp.credit.transfer.cross.border.controllers;

import com.flowpay.ccp.cip.client.CIPReply;
import com.flowpay.ccp.credit.transfer.cross.border.Constants;
import com.flowpay.ccp.credit.transfer.cross.border.CreditTransferStatus;
import com.flowpay.ccp.credit.transfer.cross.border.cip.RestartProcess;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.confirmation.ConfirmationStep;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.verify.VerifyStep;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.RestartConfirmationProcessPayload;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.RestartVerifyProcessPayload;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.datechange.RestartHandleDateChangePayload;
import com.flowpay.ccp.job.JobData;
import com.flowpay.ccp.job.JobPublisher;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Named;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import java.util.UUID;

@Path("/cip/results")
@SuppressWarnings("unused")
public class CIPController {

    JobPublisher jobPublisher;

    CIPController(@Named(Constants.BEAN_JOB_PUBLISHER_INTERNAL) JobPublisher jobPublisher) {
        this.jobPublisher = jobPublisher;
    }

    @POST
    @Path("/{status}/{id}")
    public Uni<Void> resultsReady(
            @PathParam("status") CreditTransferStatus status,
            @PathParam("id") UUID id,
            CIPReply body) {
        return jobPublisher.scheduleJob(
                new JobData(
                        UUID.randomUUID(),
                        Constants.JOB_RESTART_PROCESS,
                        new RestartProcess(status, id, body)
                )
        );
    }

    @POST
    @Path("/verify/{processID}/{idBonificoExtraSepa}/{callMade}")
    public Uni<Void> verifyCompleted(
            @PathParam("processID") UUID processID,
            @PathParam("idBonificoExtraSepa") UUID creditTransferID,
            @PathParam("callMade") VerifyStep verifyCall,
            CIPReply body) {
        return jobPublisher.scheduleJob(
                new JobData(
                        UUID.randomUUID(),
                        Constants.JOB_RESTART_VERIFY_CREDIT_TRANSFER,
                        new RestartVerifyProcessPayload(
                                creditTransferID,
                                processID,
                                true,
                                verifyCall,
                                body
                        )
                )
        );
    }

    @POST
    @Path("/confirmation/{processID}/{idBonificoExtraSepa}/{callMade}")
    public Uni<Void> confirmationCompleted(
            @PathParam("processID") UUID processID,
            @PathParam("idBonificoExtraSepa") UUID creditTransferID,
            @PathParam("callMade") ConfirmationStep verifyCall,
            CIPReply body) {
        return jobPublisher.scheduleJob(
                new JobData(
                        UUID.randomUUID(),
                        Constants.JOB_RESTART_CONFIRMED_CREDIT_TRANSFER,
                        new RestartConfirmationProcessPayload(
                                creditTransferID,
                                processID,
                                true,
                                verifyCall,
                                body
                        )
                )
        );
    }

    @POST
    @Path("/holiday_date/{processID}/{idBonificoExtraSepa}/{idAutorizzazione}/{proposedDateEpochDay}/{isPaese}")
    public Uni<Void> changeDate(
            @PathParam("processID") UUID processID,
            @PathParam("idBonificoExtraSepa") UUID creditTransferID,
            @PathParam("idAutorizzazione") UUID authorizationID,
            @PathParam("proposedDateEpochDay") Long proposedDateEpochDay,
            @PathParam("isPaese") Boolean isPaese,
            CIPReply body) {
        return jobPublisher.scheduleJob(
                new JobData(
                        UUID.randomUUID(),
                        Constants.JOB_RESTART_HANDLE_DATE_CHANGE,
                        new RestartHandleDateChangePayload(
                                creditTransferID,
                                authorizationID,
                                proposedDateEpochDay,
                                processID,
                                body,
                                isPaese,
                                false
                        )
                )
        );  
    }
}
