package com.amex.rest.creditcard;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Builder
@Document
public final class CardApplication {

    @Id
    private final String id;
    private final String taxNumber;
    private final Address address;
    private final int score;
    private final boolean validAddress;
    private final boolean issued;

}
