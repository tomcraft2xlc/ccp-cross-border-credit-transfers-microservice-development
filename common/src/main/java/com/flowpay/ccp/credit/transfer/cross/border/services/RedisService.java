package com.flowpay.ccp.credit.transfer.cross.border.services;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class RedisService {

    ReactiveRedisDataSource client;

    public RedisService(ReactiveRedisDataSource client) {
        this.client = client;
    }

    public Uni<Void> storeStatus(String key, BankInfoStatus info) {
        return client.value(BankInfoStatus.class).set(key, info);
    }

    public Uni<BankInfoStatus> getStatus(String key) {
        return client.value(BankInfoStatus.class).getdel(key);
    }


    public record BankInfoStatus(
            String bic,
            UUID creditTransferID,
            List<String> functionalities,
            String profile,
            String role,
            String abi,
            String token,
            String mainBranch,
            List<String> branches,
            String channel
    ) {

    }
}
