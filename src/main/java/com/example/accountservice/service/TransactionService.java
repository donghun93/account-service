package com.example.accountservice.service;

import com.example.accountservice.domain.Account;
import com.example.accountservice.domain.AccountUser;
import com.example.accountservice.domain.Transaction;
import com.example.accountservice.dto.TransactionDto;
import com.example.accountservice.exception.AccountException;
import com.example.accountservice.policy.TransactionIdGenerator;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.repository.AccountUserRepository;
import com.example.accountservice.repository.TransactionRepository;
import com.example.accountservice.type.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.example.accountservice.type.ErrorCode.*;
import static com.example.accountservice.type.TransactionResultType.FAIL;
import static com.example.accountservice.type.TransactionResultType.SUCCESS;
import static com.example.accountservice.type.TransactionType.CANCEL;
import static com.example.accountservice.type.TransactionType.USE;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TransactionService {
    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionIdGenerator transactionIdGenerator;

    @Transactional
    public TransactionDto useBalance(Long userId, String accountNumber, Long amount) {
        AccountUser findAccountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
        Account findAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));
        validateUseBalance(findAccountUser, findAccount, amount);

        findAccount.useBalance(amount);

        return TransactionDto.toResponse(transactionRepository.save(Transaction.builder()
                .account(findAccount)
                .transactionType(USE)
                .transactionResultType(SUCCESS)
                .amount(amount)
                .balanceSnapshot(findAccount.getBalance())
                .transactionId(transactionIdGenerator.generate())
                .transactedAt(LocalDateTime.now())
                .build()));
    }

    private void validateUseBalance(AccountUser accountUser, Account account, Long amount) {
        if (!Objects.equals(accountUser.getId(), account.getAccountUser().getId())) {
            throw new AccountException(USER_ACCOUNT_NOT_MATCHED);
        }
        if (account.getAccountStatus() != AccountStatus.IN_USE) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (account.getBalance() < amount) {
            throw new AccountException(AMOUNT_EXCEED_BALANCE);
        }
    }

    public void useBalanceFailed(String accountNumber, Long amount) {
        Account findAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        transactionRepository.save(Transaction.builder()
                .account(findAccount)
                .transactionType(USE)
                .transactionResultType(FAIL)
                .amount(amount)
                .balanceSnapshot(findAccount.getBalance())
                .transactionId(transactionIdGenerator.generate())
                .transactedAt(LocalDateTime.now())
                .build());
    }

    @Transactional
    public TransactionDto cancelBalance(String transactionId, String accountNumber, Long cancelAmount) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new AccountException(TRANSACTION_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        transactionValidate(transaction, account, cancelAmount);
        account.cancelBalance(cancelAmount);

        return TransactionDto.toResponse(
                transactionRepository.save(
                        Transaction.builder()
                                .account(account)
                                .transactionType(CANCEL)
                                .transactionResultType(SUCCESS)
                                .amount(cancelAmount)
                                .balanceSnapshot(account.getBalance())
                                .transactionId(transactionIdGenerator.generate())
                                .transactedAt(LocalDateTime.now())
                                .build()));
    }

    public TransactionDto getTransaction(String transactionId) {
        return TransactionDto.toResponse(
                transactionRepository.findByTransactionId(transactionId)
                        .orElseThrow(() -> new AccountException(TRANSACTION_NOT_FOUND))
        );
    }

    private void transactionValidate(Transaction transaction, Account account, Long cancelAmount) {
        if (!Objects.equals(transaction.getAccount().getId(), account.getId())) {
            throw new AccountException(TRANSACTION_ACCOUNT_UN_MATCH);
        }
        if (!Objects.equals(transaction.getAmount(), cancelAmount)) {
            throw new AccountException(CANCEL_MUST_FULLY);
        }
        if (transaction.getTransactedAt().isBefore(LocalDateTime.now().minusYears(1))) {
            throw new AccountException(TOO_OLD_ORDER_TO_CANCEL);
        }
    }
}
