package com.flowpay.ccp.credit.transfer.cross.border.beans;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.jms.ConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ArtemisConnectionFactory {

    @ConfigProperty(name = "ccp.jms.broker.url")
    String brokerUrl;

    @ConfigProperty(name = "ccp.jms.broker.username")
    String username;

    @ConfigProperty(name = "ccp.jms.broker.password")
    String password;

    @Produces
    ConnectionFactory factory() {
        return new ActiveMQJMSConnectionFactory(
                brokerUrl,
                username, password);
    }
}
