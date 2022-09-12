package com.example.accountservice.domain;

import com.example.accountservice.exception.AccountException;
import com.example.accountservice.type.AccountStatus;
import com.example.accountservice.type.ErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.example.accountservice.type.ErrorCode.AMOUNT_EXCEED_BALANCE;
import static com.example.accountservice.type.ErrorCode.INVALID_REQUEST;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Account extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AccountUser accountUser;

    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    private Long balance;
    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;

    @Builder
    private Account(AccountUser accountUser, String accountNumber, AccountStatus accountStatus, Long balance, LocalDateTime registeredAt, LocalDateTime unRegisteredAt) {
        this.accountUser = accountUser;
        this.accountNumber = accountNumber;
        this.accountStatus = accountStatus;
        this.balance = balance;
        this.registeredAt = registeredAt;
        this.unRegisteredAt = unRegisteredAt;
    }

    public void unRegistered() {
        this.accountStatus = AccountStatus.UNREGISTERED;
        this.unRegisteredAt = LocalDateTime.now();
    }

    public void useBalance(Long amount) {
        if(this.balance < amount) {
            throw new AccountException(AMOUNT_EXCEED_BALANCE);
        }
        this.balance -= amount;
    }

    public void cancelBalance(Long amount) {
        if(amount < 0) {
            throw new AccountException(INVALID_REQUEST);
        }
        this.balance += amount;
    }
}
