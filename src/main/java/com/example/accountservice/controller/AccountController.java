package com.example.accountservice.controller;

import com.example.accountservice.dto.*;
import com.example.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public CreateAccount.Response createAccount(@RequestBody @Valid final CreateAccount.Request request) {
        return CreateAccount.Response.toResponse(
                accountService.createAccount(request.getUserId(), request.getInitBalance()));
    }

    @DeleteMapping
    public DeleteAccount.Response deleteAccount(@RequestBody @Valid final DeleteAccount.Request request) {
        return DeleteAccount.Response.toResponse(
                accountService.deleteAccount(request.getUserId(), request.getAccountNumber()));
    }

    @GetMapping("/{userId}")
    public List<GetAccount.Response> getAccountByUserId(@PathVariable final Long userId) {
        return accountService.findAllAccount(userId).stream()
                .map(GetAccount.Response::toResponse)
                .collect(Collectors.toList());
    }
}
