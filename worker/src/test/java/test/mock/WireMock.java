package test.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;

import java.util.Map;
import java.util.UUID;

public class WireMock implements QuarkusTestResourceLifecycleManager {

    WireMockServer wireMockServer;


    public static final UUID firstResourceID = UUID.randomUUID();
    public static final UUID secondResourceID = UUID.randomUUID();

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        wireMockServer.stubFor(
                get(urlMatching("/banks/.*"))
                        .inScenario("Retry Scenario")
                        .whenScenarioStateIs(STARTED)
                        .willReturn(
                                aResponse().withStatus(420)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("""
                                                {
                                                    "resourceName": "bank_data",
                                                    "resourceID": "%s"
                                                }
                                                """.formatted(firstResourceID))

                        )
                        .willSetStateTo("Ready")
        );

        wireMockServer.stubFor(
                get(urlMatching("/banks/.*"))
                        .inScenario("Retry Scenario")
                        .whenScenarioStateIs("First Request Received")
                        .willReturn(
                                aResponse().withStatus(420)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("""
                                                {
                                                    "resourceName": "bank_data",
                                                    "resourceID": "%s"
                                                }
                                                """.formatted(secondResourceID))

                        )
                        .willSetStateTo("Ready")
        );

        wireMockServer.stubFor(
                get(urlMatching("/banks/.*"))
                        .inScenario("Retry Scenario")
                        .whenScenarioStateIs("Ready")
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("""
                                                {
                                                    "bic": "CIPBITM1XXX",
                                                    "decodificaBic": "BANCA DI TEST 1",
                                                    "paese": "IT",
                                                    "decodificaPaese": "ITALIA",
                                                    "abi": 12345,
                                                    "flagRaggiungibilitaSepa": true,
                                                    "flagTgt": true,
                                                    "flagCbpr": true,
                                                    "flagEmbargo": false
                                                }
                                                """)
                        )
        );

        wireMockServer.stubFor(
                get(urlMatching("/banks/CIPBITM1XXX"))
                        .willReturn(
                                aResponse().withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("""
                                                {
                                                    "bic": "CIPBITM1XXX",
                                                    "decodificaBic": "BANCA DI TEST 1",
                                                    "paese": "IT",
                                                    "decodificaPaese": "ITALIA",
                                                    "abi": 12345,
                                                    "flagRaggiungibilitaSepa": true,
                                                    "flagTgt": true,
                                                    "flagCbpr": true,
                                                    "flagEmbargo": false
                                                }
                                                """)
                        )
        );

        wireMockServer.stubFor(
                get(urlMatching("/banks/CIPBITM2XXX"))
                        .willReturn(
                                aResponse().withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("""
                                                {
                                                    "bic": "CIPBITM2XXX",
                                                    "decodificaBic": "BANCA DI TEST 2",
                                                    "paese": "IT",
                                                    "decodificaPaese": "ITALIA",
                                                    "abi": 12345,
                                                    "flagRaggiungibilitaSepa": true,
                                                    "flagTgt": true,
                                                    "flagCbpr": false,
                                                    "flagEmbargo": false
                                                }
                                                """)
                        )
        );

        wireMockServer.stubFor(
                get(urlMatching("/banks/CIPBITM3XXX"))
                        .willReturn(
                                aResponse().withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("""
                                                {
                                                    "bic": "CIPBITM3XXX",
                                                    "decodificaBic": "BANCA DI TEST 3",
                                                    "paese": "IT",
                                                    "decodificaPaese": "ITALIA",
                                                    "abi": 12345,
                                                    "flagRaggiungibilitaSepa": true,
                                                    "flagTgt": false,
                                                    "flagCbpr": true,
                                                    "flagEmbargo": false
                                                }
                                                """)
                        )
        );

        wireMockServer.stubFor(
                post(urlMatching("/.*"))
                        .willReturn(
                                aResponse().withStatus(200)
                        )
        );



        return Map.of(
                "quarkus.rest-client.\"ccp.registry.endpoint\".url", wireMockServer.baseUrl(),
                "quarkus.rest-client.\"ccp.cip.client\".url", wireMockServer.baseUrl()
        );
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}
