package com.example.accountservice.service;

import com.example.accountservice.aop.AccountLockId;
import com.example.accountservice.service.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LockAopAspect {

    private final LockService lockService;

    @Around("@annotation(com.example.accountservice.aop.AccountLock) && args(request)")
    public Object aroundMethod(ProceedingJoinPoint pjp, AccountLockId request) throws Throwable {
        // lock 취득 시도
        lockService.lock(request.getAccountNumber());

        try {
            return pjp.proceed();
        } finally {
            // lock 해제
            lockService.unlock(request.getAccountNumber());
        }
    }
}
