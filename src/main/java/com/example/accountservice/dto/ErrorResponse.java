package com.example.accountservice.dto;


import com.example.accountservice.type.ErrorCode;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ErrorResponse {
    private final ErrorCode errorCode;
    private final String errorMessage;

    @Builder
    private ErrorResponse(ErrorCode errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
