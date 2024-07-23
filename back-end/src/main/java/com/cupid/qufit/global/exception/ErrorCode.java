package com.cupid.qufit.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 예기치못한 에러 발생 시 반환 에러
    UNEXPECTED_ERROR("예기치 못한 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR),

    // 회원 관련
    MEMBER_DEFAULT_ERROR("회원 관련 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	SIGNUP_FAILURE("회원가입 도중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_ALREADY_EXISTS("이미 사용 중인 계정입니다.", HttpStatus.CONFLICT),
    ACCEPT_PENDING_USER("가입 승인 대기 중인 계정입니다.", HttpStatus.NOT_FOUND),
    MEMBER_NOT_FOUND("회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SIGNUP_REQUIRED("회원 가입이 필요합니다.", HttpStatus.NOT_FOUND),

    // 태그 관련
    TAG_NOT_FOUND("태그를 찾을 수 없습니다.",HttpStatus.NOT_FOUND),
    LOCATION_NOT_FOUND("지역을 찾을 수 없습니다.",HttpStatus.NOT_FOUND),

    // 채팅 관련
    CHAT_ROOM_NOT_FOUND("채팅방을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CHAT_MESSAGE_NOT_FOUND("채팅 메시지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CHAT_ROOM_MEMBER_NOT_FOUND("채팅방 멤버 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_CHAT_ACCESS("채팅방에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN), // ! 사용자가 채팅방의 멤버인지 확인하는 것.

    // 토큰 관련
    EXPIRED_TOKEN("토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_DEFAULT_ERROR("토큰 처리 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST)

    ;

    private final String message;
    private final HttpStatus httpStatus;
}
