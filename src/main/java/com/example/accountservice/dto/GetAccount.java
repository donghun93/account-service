package com.example.accountservice.dto;

import lombok.Builder;
import lombok.Getter;

public class GetAccount {
    @Getter
    public static class Response {
        private final String accountNumber;
        private final Long balance;

        @Builder
        private Response(String accountNumber, Long balance) {
            this.accountNumber = accountNumber;
            this.balance = balance;
        }

        public static Response toResponse(AccountDto accountDto) {
            return Response.builder()
                    .accountNumber(accountDto.getAccountNumber())
                    .balance(accountDto.getBalance())
                    .build();
        }
    }
}
