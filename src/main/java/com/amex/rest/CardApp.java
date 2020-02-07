package com.amex.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication
public class CardApp {

    public static void main(String[] args) {
        SpringApplication.run(CardApp.class, args);
    }

}
