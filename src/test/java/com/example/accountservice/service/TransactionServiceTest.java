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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.accountservice.type.ErrorCode.*;
import static com.example.accountservice.type.TransactionResultType.SUCCESS;
import static com.example.accountservice.type.TransactionType.CANCEL;
import static com.example.accountservice.type.TransactionType.USE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    AccountUserRepository accountUserRepository;

    @Mock
    AccountRepository accountRepository;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    TransactionIdGenerator transactionIdGenerator;

    @InjectMocks
    TransactionService transactionService;

    @Test
    @DisplayName("거래 시 사용자 없을 경우 테스트")
    void transactionUseBalanceUserNotFoundTest() {
        // given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1231231231", 5000L));
        // then
        assertThat(accountException.getErrorCode()).isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("거래 시 계좌가 없을 경우 테스트")
    void transactionUseBalanceAccountNotFoundTest() {
        // given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(AccountUser.builder().build()));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());
        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1231231231", 5000L));
        // then
        assertThat(accountException.getErrorCode()).isEqualTo(ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("사용자 아이디와 계좌 소유주가 다른 경우 테스트")
    void transactionUseBalanceUserAndAccountNotMatchedTest() {
        // given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("TEST")
                .build();

        Account account = Account.builder()
                .accountUser(AccountUser.builder().build())
                .accountNumber("1234567890")
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .registeredAt(LocalDateTime.now())
                .build();
        // given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));


        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1231231231", 1L));

        // then
        assertThat(accountException.getErrorCode()).isEqualTo(USER_ACCOUNT_NOT_MATCHED);
    }

    @Test
    @DisplayName("계좌가 이미 해지 상태인 경우 테스트")
    void transactionUseBalanceAccountStatusUnRegisteredTest() {
        // given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("TEST")
                .build();

        Account account = Account.builder()
                .accountUser(accountUser)
                .accountNumber("1234567890")
                .accountStatus(AccountStatus.UNREGISTERED)
                .balance(10000L)
                .registeredAt(LocalDateTime.now())
                .build();
        // given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));


        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1231231231", 1L));

        // then
        assertThat(accountException.getErrorCode()).isEqualTo(ACCOUNT_ALREADY_UNREGISTERED);
    }

    @Test
    @DisplayName("거래금액이 잔액보다 큰 경우")
    void transactionUseBalanceAccountOverAmountTest() {
        // given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("TEST")
                .build();

        Account account = Account.builder()
                .accountUser(accountUser)
                .accountNumber("1234567890")
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .registeredAt(LocalDateTime.now())
                .build();
        // given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));


        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L, "1231231231", 15000L));

        // then
        assertThat(accountException.getErrorCode()).isEqualTo(AMOUNT_EXCEED_BALANCE);
    }

    @Test
    @DisplayName("거래 아이디에 해당하는 거래가 없는 경우")
    void transactionCancelBalanceTransactionIdNotFoundTest() {
        // given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionId", "1234567890", 1000L));

        // then
        assertThat(accountException.getErrorCode()).isEqualTo(TRANSACTION_NOT_FOUND);
    }

    @Test
    @DisplayName("거래 취소 시 계좌가 없을 경우 테스트")
    void transactionCancelBalanceUserNotFoundTest() {
        // given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(Transaction.builder().build()));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionId", "1234567890", 1000L));

        // then
        assertThat(accountException.getErrorCode()).isEqualTo(ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("거래금액과 거래 취소 금액이 다른경우 테스트")
    void transactionCancelBalanceUserAndAccountNotMatchedTest() {
        // given
        Account account = Account.builder()
                .accountUser(AccountUser.builder().id(2L).build())
                .accountNumber("1234567890")
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .registeredAt(LocalDateTime.now())
                .build();
        // given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(Transaction.builder()
                        .amount(5000L)
                        .account(account)
                        .build()));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));


        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionId", "1234567890", 1000L));

        // then
        assertThat(accountException.getErrorCode()).isEqualTo(CANCEL_MUST_FULLY);
    }

    @Test
    @DisplayName("1년이 넘은 거래는 사용 취소 불가능 테스트")
    void transactionCancelBalanceOneYearOverNotCancelTest() {
        // given
        Account account = Account.builder()
                .accountUser(AccountUser.builder().id(2L).build())
                .accountNumber("1234567890")
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .registeredAt(LocalDateTime.now())
                .build();
        // given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(Transaction.builder()
                        .amount(5000L)
                        .account(account)
                        .transactedAt(LocalDateTime.now().minusYears(1).minusDays(1))
                        .build()));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));


        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("transactionId", "1234567890", 5000L));

        // then
        assertThat(accountException.getErrorCode()).isEqualTo(TOO_OLD_ORDER_TO_CANCEL);
    }

    @Test
    @DisplayName("거래 취소 성공 테스트")
    void cancelBalanceSuccessTest() {
        // given
        Account account = Account.builder()
                .accountUser(AccountUser.builder().id(2L).build())
                .accountNumber("1234567890")
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .registeredAt(LocalDateTime.now())
                .build();
        // given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(Transaction.builder()
                        .amount(5000L)
                        .account(account)
                        .transactedAt(LocalDateTime.now())
                        .build()));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(CANCEL)
                        .transactionResultType(SUCCESS)
                        .amount(10000L)
                        .balanceSnapshot(12000L)
                        .transactionId("transactionId")
                        .transactedAt(LocalDateTime.now())
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);


        // when
        TransactionDto transactionDto = transactionService.cancelBalance("transactionId", "1000000000", 5000L);

        // then
        verify(transactionRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getTransactionType()).isEqualTo(CANCEL);
        assertThat(captor.getValue().getAmount()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("거래 조회 시 거래 없을 경우 테스트")
    void getTransactionNotFoundTest() {
        // given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.getTransaction(anyString()));


        // then
        assertThat(accountException.getErrorCode()).isEqualTo(TRANSACTION_NOT_FOUND);
    }

    @Test
    @DisplayName("거래 조회 성공 테스트")
    void getTransactionSuccessTest() {
        // given
        Account account = Account.builder()
                .accountUser(AccountUser.builder().id(2L).build())
                .accountNumber("1234567890")
                .accountStatus(AccountStatus.IN_USE)
                .balance(10000L)
                .registeredAt(LocalDateTime.now())
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(Transaction.builder()
                        .transactionId("transactionId")
                        .transactedAt(LocalDateTime.now())
                        .balanceSnapshot(500L)
                        .transactionType(USE)
                        .transactionResultType(SUCCESS)
                        .amount(1000L)
                        .account(account)
                        .build()));

        // when
        TransactionDto transaction = transactionService.getTransaction("transactionId");


        // then
        assertThat(transaction.getTransactionType()).isEqualTo(USE);
        assertThat(transaction.getTransactionResultType()).isEqualTo(SUCCESS);
        assertThat(transaction.getAmount()).isEqualTo(1000L);

    }
}