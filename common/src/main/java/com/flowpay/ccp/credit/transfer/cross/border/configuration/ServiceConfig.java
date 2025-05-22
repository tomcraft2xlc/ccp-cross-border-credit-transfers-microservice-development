package com.flowpay.ccp.credit.transfer.cross.border.configuration;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;

@StaticInitSafe
@ConfigMapping(prefix = "ccp.self")
public interface ServiceConfig {

    String url();
}
