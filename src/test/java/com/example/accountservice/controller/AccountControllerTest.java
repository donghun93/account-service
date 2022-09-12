package com.example.accountservice.controller;

import com.example.accountservice.dto.AccountDto;
import com.example.accountservice.dto.CreateAccount;
import com.example.accountservice.dto.DeleteAccount;
import com.example.accountservice.service.AccountService;
import com.example.accountservice.type.AccountStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("계좌 생성 성공 테스트")
    void createAccountSuccessTest() throws Exception {
        // given
        given(accountService.createAccount(anyLong(), anyLong()))
                .willReturn(AccountDto.builder()
                        .accountNumber("1000000000")
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(5000L)
                        .userId(1L)
                        .registeredAt(LocalDateTime.now())
                        .build());

        // when
        // then
        mockMvc.perform(post("/account")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateAccount.Request(1L, 5000L)
                        ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.accountNumber").value("1000000000"))
                .andDo(print());
    }
    
    @Test
    @DisplayName("계좌 삭제 성공 테스트")
    void deleteAccountSuccessTest() throws Exception {
        // given
        given(accountService.deleteAccount(anyLong(), anyString()))
                .willReturn(AccountDto.builder()
                        .accountNumber("1234567890")
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .unRegisteredAt(LocalDateTime.now())
                        .balance(0L)
                        .userId(1L)
                        .build());
    
        // when
        // then
        mockMvc.perform(delete("/account")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new DeleteAccount.Request(1L, "1000000000")
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());
    }
    
    @Test
    @DisplayName("사용자 ID로 계좌 조회 테스트")
    void getAccountByUserIdSuccessTest() throws Exception {
        // given
        given(accountService.findAllAccount(anyLong()))
                .willReturn(List.of(
                        AccountDto.builder()
                                .userId(1L)
                                .balance(5000L)
                                .accountNumber("1111111111")
                                .accountStatus(AccountStatus.IN_USE)
                                .registeredAt(LocalDateTime.now())
                                .build(),
                        AccountDto.builder()
                                .userId(2L)
                                .balance(10000L)
                                .accountNumber("2222222222")
                                .accountStatus(AccountStatus.IN_USE)
                                .registeredAt(LocalDateTime.now())
                                .build(),
                        AccountDto.builder()
                                .userId(3L)
                                .balance(0L)
                                .accountNumber("3333333333")
                                .accountStatus(AccountStatus.UNREGISTERED)
                                .unRegisteredAt(LocalDateTime.now())
                                .build()
                ));
    
        // when
        // then
        mockMvc.perform(get("/account/1")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("1111111111"))
                .andExpect(jsonPath("$[0].balance").value(5000L))
                .andExpect(jsonPath("$[1].accountNumber").value("2222222222"))
                .andExpect(jsonPath("$[1].balance").value(10000L))
                .andExpect(jsonPath("$[2].accountNumber").value("3333333333"))
                .andExpect(jsonPath("$[2].balance").value(0L))
                .andDo(print());
    }
}