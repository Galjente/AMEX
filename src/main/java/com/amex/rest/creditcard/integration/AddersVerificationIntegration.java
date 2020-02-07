package com.amex.rest.creditcard.integration;

import com.amex.rest.creditcard.Address;
import com.amex.rest.creditcard.dto.AddressVerificationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AddersVerificationIntegration {

    private final String addressServer;
    private final WebClient.Builder webClientBuilder;

    public AddersVerificationIntegration(@Value("${amex.rest.address.server}") final String addressServer,
                                         final WebClient.Builder webClientBuilder) {
        this.addressServer = addressServer;
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<Boolean> retrieveAddersVerification(Address address) {
        return webClientBuilder.build()
                .post()
                .uri(addressServer + "/address/verify")
                .header("Content-Type", "application/json")
                .bodyValue(address)
                .retrieve().bodyToMono(AddressVerificationDto.class)
                .map(AddressVerificationDto::isValid)
                .onErrorResume(throwable -> {
                    log.error("Error on address verification", throwable);
                    return Mono.just(false);
                })
                .onErrorReturn(false);
    }
}
