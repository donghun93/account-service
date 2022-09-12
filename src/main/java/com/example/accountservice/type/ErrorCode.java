package com.example.accountservice.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    SERVER_ERROR("서버 에러입니다."),
    INVALID_REQUEST("잘못된 요청입니다."),
    USER_NOT_FOUND("사용자가 없습니다"),
    ACCOUNT_NOT_FOUND("등록된 계좌가 없습니다."),
    TRANSACTION_ACCOUNT_UN_MATCH("이 거래는 해당 계좌에서 발생한 거래가 아닙니다."),
    ACCOUNT_TRANSACTION_LOCK("해당 계좌는 사용 중입니다."),
    CANCEL_MUST_FULLY("부분 취소는 허용되지 않습니다."),
    TOO_OLD_ORDER_TO_CANCEL("1년이 지난 거래는 취소가 불가능합니다."),
    TRANSACTION_NOT_FOUND("해당 거래가 없습니다."),
    USER_ACCOUNT_NOT_MATCHED("계좌 소유주가 일치하지 않습니다."),
    ACCOUNT_ALREADY_UNREGISTERED("계좌가 이미 해지되었습니다."),
    ACCOUNT_BALANCE_NOT_EMPTY("잔액이 남아있습니다."),
    ACCOUNT_MAX_OVER("최대 보유 가능한 계좌 수가 넘었습니다."),
    DUPLICATED_ACCOUNT_NUMBER("계좌 번호가 중복되었습니다."),
    AMOUNT_EXCEED_BALANCE("거래 금액이 계좌 잔액보다 큽니다.")
    ;

    private final String description;
}
