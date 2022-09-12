package com.example.accountservice.service;

import com.example.accountservice.domain.Account;
import com.example.accountservice.domain.AccountUser;
import com.example.accountservice.dto.AccountDto;
import com.example.accountservice.exception.AccountException;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.repository.AccountUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.accountservice.type.AccountStatus.IN_USE;
import static com.example.accountservice.type.AccountStatus.UNREGISTERED;
import static com.example.accountservice.type.ErrorCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public AccountDto createAccount(Long userId, Long initBalance) {
        AccountUser findAccountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        accountCountValidate(findAccountUser);

        String accountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> Integer.parseInt(account.getAccountNumber()) + 1 + "")
                .orElse("1000000000");

        accountNumberDuplicateValidate(accountNumber);

        return AccountDto.toResponse(accountRepository.save(Account.builder()
                .accountUser(findAccountUser)
                .accountNumber(accountNumber)
                .accountStatus(IN_USE)
                .balance(initBalance)
                .registeredAt(LocalDateTime.now())
                .build()));
    }

    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        AccountUser findAccountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        Account findAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validateDeleteAccount(findAccountUser, findAccount);
        findAccount.unRegistered();

        return AccountDto.toResponse(findAccount);
    }

    private void validateDeleteAccount(AccountUser accountUser, Account account) {
        if (!Objects.equals(accountUser.getId(), account.getAccountUser().getId())) {
            throw new AccountException(USER_ACCOUNT_NOT_MATCHED);
        }

        if (account.getAccountStatus() == UNREGISTERED) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }

        if (account.getBalance() > 0) {
            throw new AccountException(ACCOUNT_BALANCE_NOT_EMPTY);
        }
    }

    public List<AccountDto> findAllAccount(Long userId) {
        AccountUser findAccountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        return accountRepository.findAllByAccountUser(findAccountUser)
                .stream().map(AccountDto::toResponse)
                .collect(Collectors.toList());
    }


    private void accountNumberDuplicateValidate(String accountNumber) {
        accountRepository.findByAccountNumber(accountNumber)
                .ifPresent(m -> {
                    throw new AccountException(DUPLICATED_ACCOUNT_NUMBER);
                });
    }

    private void accountCountValidate(AccountUser accountUser) {
        Integer accountCount = accountRepository.countByAccountUser(accountUser);
        if (accountCount >= 10) {
            throw new AccountException(ACCOUNT_MAX_OVER);
        }
    }
}
