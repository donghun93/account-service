package com.example.accountservice.aop;

import com.example.accountservice.controller.AccountController;
import com.example.accountservice.dto.ErrorResponse;
import com.example.accountservice.exception.AccountException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.accountservice.type.ErrorCode.INVALID_REQUEST;
import static com.example.accountservice.type.ErrorCode.SERVER_ERROR;

@Slf4j
@RestControllerAdvice(basePackageClasses = {AccountController.class})
public class AccountExceptionHandler {

    @ExceptionHandler(AccountException.class)
    public ResponseEntity<ErrorResponse> accountExceptionHandler(AccountException e) {
        log.error("accountException {}, {}", e.getErrorCode(), e.getErrorMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(getErrorResponse(e));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.error("methodArgumentNotValidException : ", e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(getErrorResponse(new AccountException(INVALID_REQUEST)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
        log.error("exception : ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(getErrorResponse(new AccountException(SERVER_ERROR)));
    }

    private ErrorResponse getErrorResponse(AccountException e) {
        return ErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .errorMessage(e.getErrorMessage())
                .build();
    }
}
