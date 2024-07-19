package com.cupid.qufit.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 회원 관련
    USERNAME_ALREADY_EXISTS("이미 사용 중인 계정입니다.", HttpStatus.CONFLICT),
    MEMBER_NOT_FOUND("회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DEFAULT("회원 관련 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCEPT_PENDING_USER("가입 승인 대기 중인 계정입니다.", HttpStatus.NOT_FOUND),
    SIGNUP_REQUIRED("회원 가입이 필요합니다.", HttpStatus.NOT_FOUND),

    // 태그 관련
    TAG_NOT_FOUND("태그를 찾을 수 없습니다.",HttpStatus.NOT_FOUND),
    LOCATION_NOT_FOUND("지역을 찾을 수 없습니다.",HttpStatus.NOT_FOUND),
    ;

    private final String message;
    private final HttpStatus httpStatus;
}
