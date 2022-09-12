package com.example.accountservice.dto;

import com.example.accountservice.domain.Transaction;
import com.example.accountservice.type.TransactionResultType;
import com.example.accountservice.type.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TransactionDto {

    private final String accountNumber;
    private final TransactionType transactionType;
    private final TransactionResultType transactionResultType;
    private final Long amount;
    private final Long balanceSnapshot;
    private final String transactionId;
    private final LocalDateTime transactedAt;

    @Builder
    private TransactionDto(String accountNumber, TransactionType transactionType, TransactionResultType transactionResultType, Long amount, Long balanceSnapshot, String transactionId, LocalDateTime transactedAt) {
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.transactionResultType = transactionResultType;
        this.amount = amount;
        this.balanceSnapshot = balanceSnapshot;
        this.transactionId = transactionId;
        this.transactedAt = transactedAt;
    }

    public static TransactionDto toResponse(Transaction transaction) {
        return TransactionDto.builder()
                .accountNumber(transaction.getAccount().getAccountNumber())
                .transactionType(transaction.getTransactionType())
                .transactionResultType(transaction.getTransactionResultType())
                .amount(transaction.getAmount())
                .balanceSnapshot(transaction.getBalanceSnapshot())
                .transactionId(transaction.getTransactionId())
                .transactedAt(transaction.getTransactedAt())
                .build();
    }
}
