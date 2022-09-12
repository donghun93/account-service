package com.example.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class DeleteAccount {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotNull
        @Min(1)
        private Long userId;

        @NotNull
        @Size(min = 10, max = 10)
        private String accountNumber;
    }

    @Getter
    public static class Response {
        private final Long userId;
        private final String accountNumber;
        private final LocalDateTime unRegisteredAt;

        @Builder
        private Response(Long userId, String accountNumber, LocalDateTime unRegisteredAt) {
            this.userId = userId;
            this.accountNumber = accountNumber;
            this.unRegisteredAt = unRegisteredAt;
        }

        public static Response toResponse(AccountDto accountDto) {
            return Response.builder()
                    .userId(accountDto.getUserId())
                    .accountNumber(accountDto.getAccountNumber())
                    .unRegisteredAt(accountDto.getUnRegisteredAt())
                    .build();
        }
    }
}
