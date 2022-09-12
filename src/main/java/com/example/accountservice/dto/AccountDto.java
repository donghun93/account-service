package com.example.accountservice.dto;

import com.example.accountservice.domain.Account;
import com.example.accountservice.type.AccountStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AccountDto {
    private final Long userId;
    private final String accountNumber;
    private final AccountStatus accountStatus;
    private final Long balance;
    private final LocalDateTime registeredAt;
    private final LocalDateTime unRegisteredAt;

    @Builder
    private AccountDto(Long userId, String accountNumber, AccountStatus accountStatus, Long balance, LocalDateTime registeredAt, LocalDateTime unRegisteredAt) {
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.accountStatus = accountStatus;
        this.balance = balance;
        this.registeredAt = registeredAt;
        this.unRegisteredAt = unRegisteredAt;
    }

    public static AccountDto toResponse(Account account) {
        return AccountDto.builder()
                .userId(account.getAccountUser().getId())
                .accountNumber(account.getAccountNumber())
                .accountStatus(account.getAccountStatus())
                .balance(account.getBalance())
                .registeredAt(account.getRegisteredAt())
                .unRegisteredAt(account.getUnRegisteredAt())
                .build();
    }
}
