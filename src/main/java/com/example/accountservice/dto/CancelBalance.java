package com.example.accountservice.dto;

import com.example.accountservice.type.TransactionResultType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class CancelBalance {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank
        private String transactionId;

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;

        @NotNull
        @Min(10)
        @Max(1000_000_000)
        private Long amount;
    }

    @Getter
    public static class Response {
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

        public static CancelBalance.Response toResponse(TransactionDto transactionDto) {
            return Response.builder()
                    .accountNumber(transactionDto.getAccountNumber())
                    .transactionResult(transactionDto.getTransactionResultType())
                    .transactionId(transactionDto.getTransactionId())
                    .amount(transactionDto.getAmount())
                    .transactedAt(transactionDto.getTransactedAt())
                    .build();
        }
    }
}
