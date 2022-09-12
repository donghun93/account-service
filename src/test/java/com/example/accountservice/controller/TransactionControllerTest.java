package com.example.accountservice.controller;

import com.example.accountservice.dto.*;
import com.example.accountservice.service.AccountService;
import com.example.accountservice.service.TransactionService;
import com.example.accountservice.type.AccountStatus;
import com.example.accountservice.type.TransactionResultType;
import com.example.accountservice.type.TransactionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.accountservice.type.TransactionResultType.SUCCESS;
import static com.example.accountservice.type.TransactionType.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("계좌 사용 테스트")
    void useBalanceSuccessTest() throws Exception {
        // given
        given(transactionService.useBalance(anyLong(), anyString(), anyLong()))
                .willReturn(TransactionDto.builder()
                        .accountNumber("1000000000")
                        .transactionResultType(SUCCESS)
                        .transactionType(CANCEL)
                        .transactionId("transactionId")
                        .amount(5000L)
                        .transactedAt(LocalDateTime.now())
                        .build());
    
        // when
        // then
        mockMvc.perform(post("/transaction/use")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new CreateBalance.Request(1L, "1231231231", 100L)
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1000000000"))
                .andExpect(jsonPath("$.transactionResult").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionId").value("transactionId"))
                .andExpect(jsonPath("$.amount").value(5000))
                .andDo(print());
    }

    @Test
    @DisplayName("계좌 취소 테스트")
    void cancelBalanceSuccessTest() throws Exception {
        // given
        given(transactionService.cancelBalance(
                anyString(), anyString(), anyLong()))
                .willReturn(TransactionDto.builder()
                        .accountNumber("7777777777")
                        .transactionResultType(SUCCESS)
                        .transactionType(CANCEL)
                        .transactionId("382djha8s2")
                        .amount(10000L)
                        .transactedAt(LocalDateTime.now())
                        .build());

        // when
        // then
        mockMvc.perform(post("/transaction/cancel")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CancelBalance.Request("1234567890", "1231231231", 100L)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("7777777777"))
                .andExpect(jsonPath("$.transactionResult").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionId").value("382djha8s2"))
                .andExpect(jsonPath("$.amount").value(10000))
                .andDo(print());
    }
    
    @Test
    @DisplayName("거래 조회 테스트")
    void queryTransactionTest() throws Exception {
        // given
        given(transactionService.getTransaction(anyString()))
                .willReturn(TransactionDto.builder()
                        .accountNumber("1000000000")
                        .transactionResultType(SUCCESS)
                        .transactionType(USE)
                        .transactionId("transactionId")
                        .amount(5000L)
                        .transactedAt(LocalDateTime.now())
                        .build());
        // when
        // then
        mockMvc.perform(get("/transaction/1")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1000000000"))
                .andExpect(jsonPath("$.transactionType").value("USE"))
                .andExpect(jsonPath("$.transactionResult").value("SUCCESS"))
                .andExpect(jsonPath("$.transactionId").value("transactionId"))
                .andExpect(jsonPath("$.amount").value(5000))
                .andDo(print());
    }
}