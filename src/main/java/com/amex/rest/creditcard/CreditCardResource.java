package com.amex.rest.creditcard;

import com.amex.rest.creditcard.dto.CreditCardAllowedRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "credit-card", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
public class CreditCardResource {

    private final int requestTimeout;
    private final CreditCardService creditCardService;

    public CreditCardResource(@Value("${amex.rest.request.timeout:60}" )final int requestTimeout, final CreditCardService creditCardService) {
        this.requestTimeout = requestTimeout;
        this.creditCardService = creditCardService;
    }

    @PostMapping("/apply")
    public Mono<ResponseEntity<CardApplication>> apply(@RequestBody CreditCardAllowedRequestDto requestDto) {
        return creditCardService
                .applyToCreditCard(requestDto.getTaxNumber(), requestDto.getAddress())
                .timeout(Duration.ofSeconds(requestTimeout), Mono.error(new RequestTimeoutException()))
                .map(ResponseEntity::ok)
                .onErrorReturn(RequestTimeoutException.class, ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
