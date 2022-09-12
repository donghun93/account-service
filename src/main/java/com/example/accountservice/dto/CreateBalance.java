package com.example.accountservice.dto;

import com.example.accountservice.aop.AccountLockId;
import com.example.accountservice.type.TransactionResultType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class CreateBalance {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request implements AccountLockId {
        @NotNull
        @Min(1)
        private Long userId;

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;

        @NotNull
        @Min(10)
        @Max(1000_000_000)
        private Long amount;
    }

    @Getter
    public static class Response implements AccountLockId {

        private final String accountNumber;
        private final TransactionResultType transactionResult;
        private final String transactionId;
        private final Long amount;
        private final LocalDateTime transactedAt;

        @Builder
        private Response(String accountNumber, TransactionResultType transactionResult, String transactionId, Long amount, LocalDateTime transactedAt) {
            this.accountNumber = accountNumber;
            this.transactionResult = transactionResult;
            this.transactionId = transactionId;
            this.amount = amount;
            this.transactedAt = transactedAt;
        }

        public static Response toResponse(TransactionDto dto) {
            return Response.builder()
                    .accountNumber(dto.getAccountNumber())
                    .transactionResult(dto.getTransactionResultType())
                    .transactionId(dto.getTransactionId())
                    .amount(dto.getAmount())
                    .transactedAt(dto.getTransactedAt())
                    .build();
        }
    }
}
