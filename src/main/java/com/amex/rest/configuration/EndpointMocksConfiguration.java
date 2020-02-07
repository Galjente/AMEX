package com.amex.rest.configuration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PreDestroy;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Profile("demo")
@Configuration
public class EndpointMocksConfiguration {

    private WireMockServer server;

    @Bean
    public void configureWireMockServer() {
        server = new WireMockServer(WireMockConfiguration.wireMockConfig().port(9999));
        server.start();
        configureScoreEndpoints();
        configureAddressEndpoints();
    }

    @PreDestroy
    public void shutdown() {
        server.shutdown();
    }

    private void configureAddressEndpoints() {
        server.stubFor(post(urlMatching("/address/verify"))
                .atPriority(5)
                .willReturn(aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody("{\"score\": false}")));

        server.stubFor(post(urlMatching("/address/verify"))
                .withRequestBody(equalToJson(
                        "{" +
                                "\"address\": \"Test\", " +
                                "\"address2\": \"Test\"," +
                                "\"city\": \"Test\"," +
                                "\"state\": \"Test\"," +
                                "\"zipCode\": \"Test\"" +
                               "}"))
                .atPriority(5)
                .willReturn(aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody("{\"valid\": true}")));
    }

    private void configureScoreEndpoints() {
        server.stubFor(get(urlMatching("/score/.*"))
                .atPriority(5)
                .willReturn(aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody("{\"score\": 0}")));
        server.stubFor(get(urlMatching("/score/123"))
                .atPriority(4)
                .willReturn(aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody("{\"score\": 800}")));
        server.stubFor(get(urlMatching("/score/321"))
                .atPriority(4)
                .willReturn(aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody("{\"score\": 700}")));
    }
}
