package com.flowpay.ccp.credit.transfer.cross.border.beans;

import com.flowpay.ccp.credit.transfer.cross.border.Constants;
import com.flowpay.ccp.job.JobData;
import com.flowpay.ccp.job.JobPublisher;
import com.flowpay.ccp.resources.poll.client.PollService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.reactive.messaging.Channel;

public class Beans {

    @Produces
    @Named(Constants.BEAN_JOB_PUBLISHER_INTERNAL)
    public JobPublisher jobPublisherInternal(@Channel(Constants.CHANNEL_INTERNAL_NAME) MutinyEmitter<JobData> emitter) {
        return new JobPublisher(emitter);
    }


    @Produces
    @ApplicationScoped
    public PollService pollService(
            @Channel(Constants.CHANNEL_INTERNAL_NAME) MutinyEmitter<JobData> emitter,
            ReactiveRedisDataSource client) {
        return new PollService(emitter, client);
    }
}
