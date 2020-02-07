package com.amex.rest.creditcard.integration;

import com.amex.rest.creditcard.dto.CreditScoreDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CreditScoreIntegration {

    private final String scoreServer;
    private final WebClient.Builder webClientBuilder;

    public CreditScoreIntegration(@Value("${amex.rest.score.server}") final String scoreServer,
                                  final WebClient.Builder webClientBuilder) {
        this.scoreServer = scoreServer;
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<Integer> retrieveCreditScore(String taxNumber) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("taxNumber", taxNumber);
        return webClientBuilder.build()
                .get()
                .uri(scoreServer + "/score/{taxNumber}", parameters)
                .header("Content-Type", "application/json")
                .retrieve().bodyToMono(CreditScoreDto.class)
                .map(CreditScoreDto::getScore)
                .onErrorResume(throwable -> {
                    log.error("Error on score fetching", throwable);
                    return Mono.just(0);
                })
                .onErrorReturn(0);
    }

}
