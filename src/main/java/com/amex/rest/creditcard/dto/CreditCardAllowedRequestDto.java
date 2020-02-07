package com.amex.rest.creditcard.dto;

import com.amex.rest.creditcard.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardAllowedRequestDto {

    private String taxNumber;
    private Address address;


}
