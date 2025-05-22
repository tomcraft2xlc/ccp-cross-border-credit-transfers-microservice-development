package com.flowpay.ccp.credit.transfer.cross.border.configuration;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import io.smallrye.config.WithParentName;

import java.util.Map;
import java.util.Optional;

@ConfigMapping(prefix = "ccp.bank")
public interface BanksConfig {

    @WithParentName
    Map<String, BankConfig> bank();



    interface BankConfig {
        String bic();
        String name();
        String lei();


        PostalAddressInfo address();
        SettlementInfo channel();

        @WithName("default-authorization-levels")
        @WithDefault("2")
        Long defaultAuthorizationLevels();

        @WithName("exchange-rate-table")
        @WithDefault("false")
        Boolean listinoCommissioni();
        @WithName("sconfinamento")
        @WithDefault("false")
        Boolean sconfinamento();

        interface PostalAddressInfo {
            @WithName("indirizzo")
            Optional<String> strtNm();
            @WithName("citta")
            Optional<String> twnNm();
            @WithName("cap")
            Optional<String> cap();
            @WithName("paese")
            Optional<String> ctry();
            @WithName("provincia")
            Optional<String> provincia();

        }

        interface SettlementInfo {
            Detail t2();
            Detail cbpr();

            interface Detail {
                @WithName("enabled")
                @WithDefault("false")
                Boolean attivo();

                @WithName("mediated-by")
                Optional<String> tramitatoDa();

                @WithName("cut-off-hour")
                Optional<Integer> oraCutOff();

                @WithName("cut-off-minute")
                Optional<Integer> minutoCutOff();
            }
        }
    }
}
