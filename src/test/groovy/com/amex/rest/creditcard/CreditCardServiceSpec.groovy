package com.amex.rest.creditcard

import com.amex.rest.creditcard.integration.AddersVerificationIntegration
import com.amex.rest.creditcard.integration.CreditScoreIntegration
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Subject

class CreditCardServiceSpec extends Specification {

    private static final def DEFAULT_TAX_NUMBER = "432342"
    private static final def DEFAULT_ADDRESS = Address.builder()
            .address("Test")
            .address2("Test2")
            .city("Test3")
            .state("Test4")
            .zipCode("Test5")
            .build()

    def creditScoreThreshold = 750
    def addersVerificationIntegration = Mock(AddersVerificationIntegration)
    def creditScoreIntegration = Mock(CreditScoreIntegration)
    def cardApplicationRepository = Mock(CardApplicationRepository)

    @Subject
    def creditCardService = new CreditCardService(creditScoreThreshold, addersVerificationIntegration, creditScoreIntegration, cardApplicationRepository)

    def 'should issue credit card and save application'() {
        setup:
            def score = 800
        when:
            def creditCardApplication = creditCardService.applyToCreditCard(DEFAULT_TAX_NUMBER, DEFAULT_ADDRESS).block()

        then:
            1 * creditScoreIntegration.retrieveCreditScore(DEFAULT_TAX_NUMBER) >> Mono.just(score)
            1 * addersVerificationIntegration.retrieveAddersVerification(DEFAULT_ADDRESS) >> Mono.just(true)
            1 * cardApplicationRepository.save(_ as CardApplication) >> { args ->
                CardApplication cardApplication = args[0]
                assert cardApplication.address == DEFAULT_ADDRESS
                assert cardApplication.taxNumber == DEFAULT_TAX_NUMBER
                assert cardApplication.score == score
                assert cardApplication.validAddress
                assert cardApplication.issued
            }
        assert creditCardApplication.address == DEFAULT_ADDRESS
        assert creditCardApplication.taxNumber == DEFAULT_TAX_NUMBER
        assert creditCardApplication.score == score
        assert creditCardApplication.validAddress
        assert creditCardApplication.issued
    }

    def 'should reject credit card due low score and save application'() {
        setup:
        def score = 700
        def taxNumber = "123456"
        def address = Address.builder()
                .address("Test")
                .address2("Test2")
                .city("Test3")
                .state("Test4")
                .zipCode("Test5")
                .build()
        when:
        def creditCardApplication = creditCardService.applyToCreditCard(taxNumber, address).block()

        then:
        1 * creditScoreIntegration.retrieveCreditScore(taxNumber) >> Mono.just(score)
        1 * addersVerificationIntegration.retrieveAddersVerification(address) >> Mono.just(true)
        1 * cardApplicationRepository.save(_ as CardApplication) >> { args ->
            CardApplication cardApplication = args[0]
            assert cardApplication.address == address
            assert cardApplication.taxNumber == taxNumber
            assert cardApplication.score == score
            assert cardApplication.validAddress
            assert !cardApplication.issued
        }
        assert creditCardApplication.address == address
        assert creditCardApplication.taxNumber == taxNumber
        assert creditCardApplication.score == score
        assert creditCardApplication.validAddress
        assert !creditCardApplication.issued
    }

    def 'should reject credit card due not verified address and save application'() {
        setup:
        def score = 700
        def taxNumber = "123456"
        def address = Address.builder()
                .address("Test")
                .address2("Test2")
                .city("Test3")
                .state("Test4")
                .zipCode("Test5")
                .build()
        when:
        def creditCardApplication = creditCardService.applyToCreditCard(taxNumber, address).block()

        then:
        1 * creditScoreIntegration.retrieveCreditScore(taxNumber) >> Mono.just(score)
        1 * addersVerificationIntegration.retrieveAddersVerification(address) >> Mono.just(false)
        1 * cardApplicationRepository.save(_ as CardApplication) >> { args ->
            CardApplication cardApplication = args[0]
            assert cardApplication.address == address
            assert cardApplication.taxNumber == taxNumber
            assert cardApplication.score == score
            assert !cardApplication.validAddress
            assert !cardApplication.issued
        }
        assert creditCardApplication.address == address
        assert creditCardApplication.taxNumber == taxNumber
        assert creditCardApplication.score == score
        assert !creditCardApplication.validAddress
        assert !creditCardApplication.issued
    }
}
