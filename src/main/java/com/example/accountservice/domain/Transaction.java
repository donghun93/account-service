package com.example.accountservice.domain;

import com.example.accountservice.type.TransactionResultType;
import com.example.accountservice.type.TransactionType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Transaction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionResultType transactionResultType;

    private Long amount;
    private Long balanceSnapshot;
    private String transactionId;
    private LocalDateTime transactedAt;

    @Builder
    private Transaction(Account account, TransactionType transactionType, TransactionResultType transactionResultType, Long amount, Long balanceSnapshot, String transactionId, LocalDateTime transactedAt) {
        this.account = account;
        this.transactionType = transactionType;
        this.transactionResultType = transactionResultType;
        this.amount = amount;
        this.balanceSnapshot = balanceSnapshot;
        this.transactionId = transactionId;
        this.transactedAt = transactedAt;
    }
}
