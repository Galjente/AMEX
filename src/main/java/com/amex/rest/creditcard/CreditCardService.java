package com.amex.rest.creditcard;

import com.amex.rest.creditcard.integration.AddressVerificationIntegration;
import com.amex.rest.creditcard.integration.CreditScoreIntegration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CreditCardService {

    private final int creditScoreThreshold;

    private final AddressVerificationIntegration addressVerificationIntegration;
    private final CreditScoreIntegration creditScoreIntegration;
    private final CardApplicationRepository cardApplicationRepository;

    public CreditCardService(@Value("${amex.rest.score.threshold}") final int creditScoreThreshold,
                             final AddressVerificationIntegration addressVerificationIntegration,
                             final CreditScoreIntegration creditScoreIntegration,
                             final CardApplicationRepository cardApplicationRepository) {
        this.creditScoreThreshold = creditScoreThreshold;
        this.addressVerificationIntegration = addressVerificationIntegration;
        this.creditScoreIntegration = creditScoreIntegration;
        this.cardApplicationRepository = cardApplicationRepository;
    }

    public Mono<CardApplication> applyToCreditCard(String taxNumber, Address address) {
        return creditScoreIntegration.retrieveCreditScore(taxNumber)
                .zipWith(addressVerificationIntegration.retrieveAddersVerification(address))
                .map(tuple -> CardApplication.builder()
                        .taxNumber(taxNumber)
                        .address(address)
                        .score(tuple.getT1())
                        .validAddress(tuple.getT2())
                        .issued(isCardApproved(tuple.getT1(), tuple.getT2()))
                        .build())
                .defaultIfEmpty(CardApplication.builder()
                        .taxNumber(taxNumber)
                        .address(address)
                        .score(0)
                        .validAddress(false)
                        .issued(false)
                        .build())
                .doOnNext(this::saveApplications);
    }

    private boolean isCardApproved(Integer score, Boolean validAddress) {
        return score > creditScoreThreshold && validAddress;
    }

    private Mono<CardApplication> saveApplications(CardApplication cardApplication) {
        return cardApplicationRepository.save(cardApplication);
    }
}
