package com.amex.rest.creditcard

import com.amex.rest.creditcard.dto.CreditCardAllowedRequestDto
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Subject

class CreditCardResourceSpec extends Specification {

    private static final def DEFAULT_REQUEST = CreditCardAllowedRequestDto.builder()
            .taxNumber("12345")
            .address(Address.builder().build())
            .build()

    def requestTimeout = 2
    def creditCardService = Mock(CreditCardService)

    @Subject
    def creditCardResource = new CreditCardResource(requestTimeout, creditCardService)

    def 'should issue card'() {
        setup:
            def expectedCardApplication = CardApplication.builder()
                    .taxNumber(DEFAULT_REQUEST.taxNumber)
                    .address(DEFAULT_REQUEST.address)
                    .score(800)
                    .validAddress(true)
                    .issued(true)
                    .build()
        when:
            def response = creditCardResource.apply(DEFAULT_REQUEST).block()

        then:
            1 * creditCardService.applyToCreditCard(DEFAULT_REQUEST.taxNumber, DEFAULT_REQUEST.address) >> Mono.just(expectedCardApplication)
            response.statusCodeValue == HttpStatus.OK.value()
            response.body == expectedCardApplication
    }

    def 'should return not found'() {
        when:
            def response = creditCardResource.apply(DEFAULT_REQUEST).block()

        then:
            1 * creditCardService.applyToCreditCard(DEFAULT_REQUEST.taxNumber, DEFAULT_REQUEST.address) >> Mono.empty()
            response.statusCodeValue == HttpStatus.NOT_FOUND.value()
    }

    def 'should return request timeout'() {
        when:
            def response = creditCardResource.apply(DEFAULT_REQUEST).block()

        then:
            1 * creditCardService.applyToCreditCard(DEFAULT_REQUEST.taxNumber, DEFAULT_REQUEST.address) >> Mono.never()
            response.statusCodeValue == HttpStatus.REQUEST_TIMEOUT.value()
    }
}
