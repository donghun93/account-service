package com.example.accountservice.dto;

import com.example.accountservice.type.TransactionResultType;
import com.example.accountservice.type.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class QueryTransactionResponse {
    private final String accountNumber;
    private final TransactionType transactionType;
    private final TransactionResultType transactionResult;
    private final String transactionId;
    private final Long amount;
    private final LocalDateTime transactedAt;

    @Builder
    private QueryTransactionResponse(String accountNumber, TransactionType transactionType, TransactionResultType transactionResult, String transactionId, Long amount, LocalDateTime transactedAt) {
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.transactionResult = transactionResult;
        this.transactionId = transactionId;
        this.amount = amount;
        this.transactedAt = transactedAt;
    }

    public static QueryTransactionResponse toResponse(TransactionDto transactionDto) {
        return QueryTransactionResponse.builder()
                .accountNumber(transactionDto.getAccountNumber())
                .transactionType(transactionDto.getTransactionType())
                .transactionResult(transactionDto.getTransactionResultType())
                .transactionId(transactionDto.getTransactionId())
                .amount(transactionDto.getAmount())
                .transactedAt(transactionDto.getTransactedAt())
                .build();
    }
}
