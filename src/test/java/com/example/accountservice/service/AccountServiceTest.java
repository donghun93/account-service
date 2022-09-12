package com.example.accountservice.service;

import com.example.accountservice.domain.Account;
import com.example.accountservice.domain.AccountUser;
import com.example.accountservice.dto.AccountDto;
import com.example.accountservice.exception.AccountException;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.repository.AccountUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.accountservice.type.AccountStatus.IN_USE;
import static com.example.accountservice.type.AccountStatus.UNREGISTERED;
import static com.example.accountservice.type.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    AccountUserRepository accountUserRepository;
    @Mock
    AccountRepository accountRepository;


    @InjectMocks
    AccountService accountService;

    @Test
    @DisplayName("계좌 생성 유저 없을 경우 테스트")
    void createNotUserAccountTest() {
        // given
        given(accountUserRepository.findById(anyLong()))
                .willThrow(AccountException.class);

        // when
        assertThrows(AccountException.class,
                () -> accountService.createAccount(0L, 5000L));
    }


    @Test
    @DisplayName("계좌 생성 시 10개 초과할 경우 테스트")
    void createAccountOverTest() {
        // given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("TEST")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));

        given(accountRepository.countByAccountUser(accountUser))
                .willReturn(11);

        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.createAccount(accountUser.getId(), 5000L));

        // then
        assertThat(accountException.getErrorCode()).isEqualTo(ACCOUNT_MAX_OVER);
    }

    @Test
    @DisplayName("계좌 생성 시 계좌번호 중복 테스트")
    void createAccountNumberDuplicatedTest() {
        // given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(AccountUser.builder().build()));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder().build()));

        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.createAccount(0L, 5000L));

        // then
        assertThat(accountException.getErrorCode()).isEqualTo(DUPLICATED_ACCOUNT_NUMBER);

    }

    @Test
    @DisplayName("계좌 생성 테스트")
    void createAccountSuccessTest() {
        // given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("TEST")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));

        given(accountRepository.countByAccountUser(accountUser))
                .willReturn(1);

        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                        .accountUser(accountUser)
                        .accountNumber("1234567890")
                        .build()));

        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(accountUser)
                        .accountNumber("1231231231")
                        .balance(10000L)
                        .build());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        // when
        AccountDto account = accountService.createAccount(1L, 5000L);

        // then
        verify(accountRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getAccountStatus()).isEqualTo(IN_USE);
        assertThat(captor.getValue().getAccountUser().getId()).isEqualTo(account.getUserId());
        assertThat(captor.getValue().getBalance()).isEqualTo(5000L);
        assertThat(captor.getValue().getAccountNumber()).isEqualTo("1234567891");
    }

    @Test
    @DisplayName("계좌 해지 시 사용자가 없을 경우 테스트")
    void deleteAccountUserNotFoundTest() {
        // given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(0L, "1234567890"));

        // then
        assertThat(accountException.getErrorCode()).isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("계좌 해지 시 계좌가 없을 경우 테스트")
    void deleteAccountAccountNotFoundTest() {
        // given
        given(accountUserRepository.findById(any()))
                .willReturn(Optional.of(AccountUser.builder().build()));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(0L, "1234567890"));

        // then
        assertThat(accountException.getErrorCode()).isEqualTo(ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("계좌 삭제 시 소유주가 다를경우 테스트")
    void deleteAccountUserAndAccountNotMatchedTest() {
        // given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("TEST")
                .build();
        Account account = Account.builder()
                .accountUser(AccountUser.builder()
                        .id(10L)
                        .build())
                .accountNumber("1111111111")
                .accountStatus(IN_USE)
                .balance(10000L)
                .registeredAt(LocalDateTime.now())
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));


        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(accountUser.getId(), account.getAccountNumber()));

        // then
        assertThat(accountException.getErrorCode()).isEqualTo(USER_ACCOUNT_NOT_MATCHED);
    }

    @Test
    @DisplayName("계좌 삭제 시 이미 계좌가 해지된 경우 테스트")
    void deleteAccountUnRegisteredTest() {
        // given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("TEST")
                .build();
        Account account = Account.builder()
                .accountUser(accountUser)
                .accountNumber("1111111111")
                .accountStatus(UNREGISTERED)
                .balance(10000L)
                .registeredAt(LocalDateTime.now())
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(0L, "1234567890"));

        // then
        assertThat(accountException.getErrorCode()).isEqualTo(ACCOUNT_ALREADY_UNREGISTERED);
    }

    @Test
    @DisplayName("계좌 삭제 시 잔액이 남아있는 경우 테스트")
    void deleteAccountBalanceNotEmptyTest() {
        // given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("TEST")
                .build();
        Account account = Account.builder()
                .accountUser(accountUser)
                .accountNumber("1111111111")
                .accountStatus(IN_USE)
                .balance(10000L)
                .registeredAt(LocalDateTime.now())
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        // when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(0L, "1234567890"));

        // then
        assertThat(accountException.getErrorCode()).isEqualTo(ACCOUNT_BALANCE_NOT_EMPTY);
    }

    @Test
    @DisplayName("계좌 해지 테스트")
    void deleteAccountSuccessTest() {
        // given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("TEST")
                .build();
        Account account = Account.builder()
                .accountUser(accountUser)
                .accountNumber("1111111111")
                .accountStatus(IN_USE)
                .balance(0L)
                .registeredAt(LocalDateTime.now())
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        // when
        AccountDto accountDto = accountService.deleteAccount(1L, "1231231231");

        // then
        assertThat(accountDto.getUserId()).isEqualTo(1L);
        assertThat(accountDto.getAccountNumber()).isEqualTo("1111111111");
        assertThat(accountDto.getAccountStatus()).isEqualTo(UNREGISTERED);
        assertThat(accountDto.getBalance()).isEqualTo(0L);
    }

    @Test
    @DisplayName("계좌 조회 시 사용자 없을 경우 테스트")
    void findAllAccountUserNotFoundTest() {
        // given
        given(accountUserRepository.findById(any()))
                .willReturn(Optional.empty());

        // when
        AccountException accountException =
                assertThrows(AccountException.class, () -> accountService.findAllAccount(1L));

        // then
        assertThat(accountException.getErrorCode()).isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("계좌 조회 테스트")
    void findAllAccountSuccessTest() {
        // given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("TEST")
                .build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));
        given(accountRepository.findAllByAccountUser(accountUser))
                .willReturn(List.of(
                        Account.builder()
                                .accountUser(accountUser)
                                .accountNumber("1111111111")
                                .accountStatus(IN_USE)
                                .balance(5000L)
                                .registeredAt(LocalDateTime.now())
                                .build(),
                        Account.builder()
                                .accountUser(accountUser)
                                .accountNumber("2222222222")
                                .accountStatus(IN_USE)
                                .balance(10000L)
                                .registeredAt(LocalDateTime.now())
                                .build(),
                        Account.builder()
                                .accountUser(accountUser)
                                .accountNumber("3333333333")
                                .accountStatus(IN_USE)
                                .balance(20000L)
                                .registeredAt(LocalDateTime.now())
                                .build()
                ));

        // when
        List<AccountDto> accountDtoList = accountService.findAllAccount(accountUser.getId());

        // then
        assertThat(accountDtoList.size()).isEqualTo(3);
        assertThat(accountDtoList.get(0).getUserId()).isEqualTo(1L);
        assertThat(accountDtoList.get(0).getAccountNumber()).isEqualTo("1111111111");
        assertThat(accountDtoList.get(0).getBalance()).isEqualTo(5000L);
        assertThat(accountDtoList.get(0).getAccountStatus()).isEqualTo(IN_USE);
        assertThat(accountDtoList.get(1).getAccountNumber()).isEqualTo("2222222222");
        assertThat(accountDtoList.get(1).getBalance()).isEqualTo(10000L);
        assertThat(accountDtoList.get(1).getAccountStatus()).isEqualTo(IN_USE);
        assertThat(accountDtoList.get(2).getAccountNumber()).isEqualTo("3333333333");
        assertThat(accountDtoList.get(2).getBalance()).isEqualTo(20000L);
        assertThat(accountDtoList.get(2).getAccountStatus()).isEqualTo(IN_USE);
    }
}