package com.flowpay.ccp.credit.transfer.cross.border.beans;

import com.flowpay.ccp.credit.transfer.cross.border.Constants;
import com.flowpay.ccp.job.JobData;
import com.flowpay.ccp.job.JobPublisher;
import com.flowpay.ccp.job.JobSubscriber;
import com.flowpay.ccp.resources.poll.client.PollService;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;

public class Beans {

    @Produces
    @ApplicationScoped
    public PollService pollService(@Channel(Constants.CHANNEL_INTERNAL_NAME) MutinyEmitter<JobData> emitter, ReactiveRedisDataSource client) {
        return new PollService(emitter, client);
    }

    @Produces
    @Named(Constants.BEAN_JOB_SUBSCRIBER_INTERNAL)
    @ApplicationScoped
    public JobSubscriber jobSubscriberInternal(@Channel(Constants.CHANNEL_INTERNAL_NAME) Multi<Message<JobData>> emitter) {
        return new JobSubscriber(emitter);
    }

    @Produces
    @Named(Constants.BEAN_JOB_PUBLISHER_INTERNAL)
    @ApplicationScoped
    public JobPublisher jobPublisherInternal(@Channel(Constants.CHANNEL_INTERNAL_NAME) MutinyEmitter<JobData> emitter) {
        return new JobPublisher(emitter);
    }

}
