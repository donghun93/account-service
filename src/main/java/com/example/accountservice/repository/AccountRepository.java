package com.example.accountservice.repository;

import com.example.accountservice.domain.Account;
import com.example.accountservice.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Integer countByAccountUser(AccountUser accountUser);

    Optional<Account> findFirstByOrderByIdDesc();

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findAllByAccountUser(AccountUser accountUser);
}
