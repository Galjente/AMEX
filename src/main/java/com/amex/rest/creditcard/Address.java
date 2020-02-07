package com.amex.rest.creditcard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String address;
    private String address2;
    private String city;
    private String state;
    private String zipCode;

}
