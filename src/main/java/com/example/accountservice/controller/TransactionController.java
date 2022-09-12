package com.example.accountservice.controller;

import com.example.accountservice.aop.AccountLock;
import com.example.accountservice.dto.CancelBalance;
import com.example.accountservice.dto.CreateBalance;
import com.example.accountservice.dto.QueryTransactionResponse;
import com.example.accountservice.exception.AccountException;
import com.example.accountservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/use")
    @AccountLock
    public CreateBalance.Response useBalance(
            @RequestBody @Valid CreateBalance.Request request) {
        try {
            return CreateBalance.Response.toResponse(
                    transactionService.useBalance(request.getUserId(), request.getAccountNumber(),
                            request.getAmount())
            );
        } catch (AccountException e) {
            log.error("Failed to use balance. ");
            transactionService.useBalanceFailed(request.getAccountNumber(), request.getAmount());
            throw e;
        }
    }

    @PostMapping("/cancel")
    @AccountLock
    public CancelBalance.Response cancelBalance(
            @Valid @RequestBody CancelBalance.Request request
    ) {
        try {
            return CancelBalance.Response.toResponse(
                    transactionService.cancelBalance(request.getTransactionId(),
                            request.getAccountNumber(), request.getAmount())
            );
        } catch (AccountException e) {
            log.error("Failed to use balance. ");
            transactionService.useBalanceFailed(request.getAccountNumber(), request.getAmount());
            throw e;
        }
    }

    @GetMapping("/{transactionId}")
    public QueryTransactionResponse queryTransaction(
            @PathVariable String transactionId) {
        return QueryTransactionResponse.toResponse(
                transactionService.getTransaction(transactionId));
    }
}
