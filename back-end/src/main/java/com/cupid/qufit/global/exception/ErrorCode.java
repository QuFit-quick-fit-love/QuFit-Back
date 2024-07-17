package com.cupid.qufit.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USERNAME_ALREADY_EXISTS("이미 사용 중인 계정입니다.", HttpStatus.CONFLICT),
    MEMBER_NOT_FOUND("회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DEFAULT("회원 관련 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus httpStatus;
}
