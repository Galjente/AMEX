package com.amex.rest.creditcard;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardApplicationRepository extends ReactiveCrudRepository<CardApplication, String> {
}
