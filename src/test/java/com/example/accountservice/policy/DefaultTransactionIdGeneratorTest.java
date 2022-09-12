package com.example.accountservice.policy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DefaultTransactionIdGeneratorTest {

    DefaultTransactionIdGenerator defaultTransactionIdGenerator =
            new DefaultTransactionIdGenerator();

    @Test
    @DisplayName("거래 아이디 생성 테스트")
    void transactionIdGeneratedTest() {

        // when
        String transactionId = defaultTransactionIdGenerator.generate();

        // then
        assertThat(transactionId.length()).isEqualTo(10);
        assertFalse(transactionId.contains("-"));
    }
}