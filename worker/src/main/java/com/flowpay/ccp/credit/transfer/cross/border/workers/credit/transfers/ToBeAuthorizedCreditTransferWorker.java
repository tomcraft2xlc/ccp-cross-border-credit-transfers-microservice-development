package com.flowpay.ccp.credit.transfer.cross.border.workers.credit.transfers;

import com.flowpay.ccp.auth.client.JobAuthenticationMechanism;
import com.flowpay.ccp.credit.transfer.cross.border.Constants;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.ServiceConfig;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.BonificoExtraSepa;
import com.flowpay.ccp.credit.transfer.cross.border.services.CreditTransferService;
import com.flowpay.ccp.credit.transfer.cross.border.workers.dto.HandleCreditTransferPayload;
import com.flowpay.ccp.job.JobData;
import com.flowpay.ccp.job.JobPublisher;
import com.flowpay.ccp.job.JobSubscriber;
import com.flowpay.ccp.persistence.DataSources;
import com.flowpay.ccp.resources.poll.client.PollService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

import java.net.URL;

@ApplicationScoped
public class ToBeAuthorizedCreditTransferWorker {

    private static final Logger LOG = Logger.getLogger(ToBeAuthorizedCreditTransferWorker.class);

    final JobSubscriber subscriber;
    final CreditTransferService service;
    final JobPublisher jobPublisher;
    final JobPublisher externalJobPublisher;
    final PollService pollService;
    final DataSources dataSources;
    final URL registryEndpoint;
    final ServiceConfig serviceConfig;
    final BanksConfig banksConfig;
    final ReactiveRedisDataSource redisDataSource;

    ToBeAuthorizedCreditTransferWorker(
            @Named(Constants.BEAN_JOB_SUBSCRIBER_INTERNAL) JobSubscriber subscriber,
            CreditTransferService service,
            @Channel(Constants.CHANNEL_INTERNAL_NAME) MutinyEmitter<JobData> internalEmitter,
            @Channel(Constants.FTP_SERVICE_CHANNEL_NAME) MutinyEmitter<JobData> externalEmitter,
            PollService pollService,
            DataSources dataSources,
            @ConfigProperty(name = "quarkus.rest-client.\"ccp.registry.endpoint\".url") URL registryEndpoint,
            ServiceConfig serviceConfig,
            BanksConfig banksConfig,
            ReactiveRedisDataSource redisDataSource
    ) {
        this.subscriber = subscriber;
        this.service = service;
        this.externalJobPublisher = new JobPublisher(externalEmitter);
        this.jobPublisher = new JobPublisher(internalEmitter);
        this.pollService = pollService;
        this.dataSources = dataSources;
        this.registryEndpoint = registryEndpoint;
        this.serviceConfig = serviceConfig;
        this.banksConfig = banksConfig;
        this.redisDataSource = redisDataSource;
    }

    public void onStart(@Observes StartupEvent event) {
        LOG.info("Starting ToBeAuthorizedCreditTransferWorker");
        this.subscriber.subscribe(
                Constants.JOB_TO_BE_AUTHORIZED_CREDIT_TRANSFER,
                new HandleCreditTransferPayload.Deserializer(),
                pollService.jobWithPollableData(Constants.JOB_RESTART_TO_BE_AUTHORIZED_CREDIT_TRANSFER, this::processAuthorizedCreditTransfer)
        );
    }

    public Uni<Void> processAuthorizedCreditTransfer(HandleCreditTransferPayload payload) {
        var entity = new BonificoExtraSepa.Entity();
        var identity = JobAuthenticationMechanism.getAuthenticatedIdentity();
        var connection = this.dataSources.dataSource(identity);
        var repository = entity.repository(connection);
        return repository.getByIdLocked(payload.idBonificoExtraSepa())
        .flatMap(bonifico -> this.service.authorizedStateTransition(bonifico, connection, identity).replaceWith(bonifico))
        .call(bonifico -> repository.unlock(bonifico.id()))
        .replaceWithVoid();
    }
}
