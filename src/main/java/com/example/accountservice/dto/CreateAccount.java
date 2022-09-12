package com.example.accountservice.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateAccount {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotNull
        @Min(1)
        private Long userId;

        @NotNull
        @Min(0)
        private Long initBalance;
    }

    @Getter
    public static class Response {
        private final Long userId;
        private final String accountNumber;
        private final LocalDateTime registeredAt;

        @Builder
        private Response(Long userId, String accountNumber, LocalDateTime registeredAt) {
            this.userId = userId;
            this.accountNumber = accountNumber;
            this.registeredAt = registeredAt;
        }

        public static Response toResponse(AccountDto accountDto) {
            return Response.builder()
                    .userId(accountDto.getUserId())
                    .accountNumber(accountDto.getAccountNumber())
                    .registeredAt(accountDto.getRegisteredAt())
                    .build();
        }
    }
}
