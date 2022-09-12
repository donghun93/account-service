package com.example.accountservice.policy;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DefaultTransactionIdGenerator implements TransactionIdGenerator {

    private static final int MAX_UUID_GENERATE_LENGTH = 10;

    @Override
    public String generate() {
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, MAX_UUID_GENERATE_LENGTH);
    }
}
