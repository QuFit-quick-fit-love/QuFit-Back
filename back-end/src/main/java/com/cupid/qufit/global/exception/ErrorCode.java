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
    ACCEPT_PENDING_USER("가입 승인 대기 중인 계정입니다.", HttpStatus.UNAUTHORIZED),
    ACCEPT_REJECTED_USER("가입 승인 거절된 계정입니다.", HttpStatus.FORBIDDEN),
    MEMBER_NOT_FOUND("회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SIGNUP_REQUIRED("회원 가입이 필요합니다.", HttpStatus.UNAUTHORIZED),
    TYPE_PROFILES_NOT_FOUND("이상형 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MEMBER_ALREADY_WITHDRAWN("이미 탈퇴한 회원입니다.", HttpStatus.BAD_REQUEST),
    MEMBER_STATUS_NOT_PENDING("회원이 대기 중 상태가 아닙니다.", HttpStatus.BAD_REQUEST),
    NOT_ADMIN_MEMBER("관리자가 아닙니다.", HttpStatus.FORBIDDEN),

    // 태그 관련
    TAG_NOT_FOUND("태그를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    LOCATION_NOT_FOUND("지역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 채팅 관련
    CHAT_ROOM_NOT_FOUND("채팅방을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CHAT_MESSAGE_NOT_FOUND("채팅 메시지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CHAT_ROOM_MEMBER_NOT_FOUND("채팅방 멤버 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_CHAT_ACCESS("채팅방에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN), // ! 사용자가 채팅방의 멤버인지 확인하는 것.
    CHAT_ROOM_ALREADY_EXISTS("이미 해당 사용자와 채팅방이 존재합니다.", HttpStatus.CONFLICT),

    // 인증,인가 관련
    EXPIRED_TOKEN("토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    MALFORMED_TOKEN("잘못된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_TOKEN("지원하지 않는 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_DEFAULT_ERROR("토큰 처리 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED_ERROR("접근할 수 없습니다.", HttpStatus.FORBIDDEN),
    REFRESH_TOKEN_NOT_FOUND("RefreshToken을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNSUPPORTED_ENCODING("지원되지 않는 인코딩입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ALREADY_LOGOUT_TOKEN("이미 로그아웃 처리된 토큰입니다.", HttpStatus.BAD_REQUEST),

    // Elasticsearch Index 관련
    INDEX_ALREADY_EXISTS("이미 존재하는 이름의 인덱스입니다.", HttpStatus.CONFLICT),
    INDEX_NOT_FOUND("해당 인덱스 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ES_IO_ERROR("입출력 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR),

    // ES VideoRoom 관련

    // 미팅룸 관련
    VIDEO_ROOM_NOT_FOUND("미팅룸을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PARTICIPANT_NOT_FOUND("참가자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PARTICIPANT_ALREADY_EXISTS("이미 해당 참가자가 참여중인 방입니다.", HttpStatus.CONFLICT),
    HOST_NOT_FOUND("방장을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_PAGE_REQUEST("잘못된 페이지 요청입니다.", HttpStatus.BAD_REQUEST),
    NOT_ROOM_HOST("방장이 아닙니다.", HttpStatus.FORBIDDEN),
    PARTICIPANT_COUNT_MISMATCH("현재 남자, 여자 인원이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_STATUS_TYPE("유효하지 않은 방 상태입니다.", HttpStatus.BAD_REQUEST),
    MAX_PARTICIPANTS_REACHED("이미 최대 인원입니다.", HttpStatus.CONFLICT),

    // 친구 관련
    FRIEND_ALREADY_EXISTS("이미 해당 멤버와 친구 관계입니다.", HttpStatus.CONFLICT),
    FRIEND_NOT_FOUND("해당 멤버와 친구 관계가 아닙니다.", HttpStatus.NOT_FOUND),
    FRIEND_ALREADY_INACTIVE("이미 비활성화된 친구 입니다.", HttpStatus.CONFLICT),

    // S3 관련
    EMPTY_FILE("파일이 비어있습니다.", HttpStatus.BAD_REQUEST),
    EMPTY_FILE_EXTENSION("파일 확장자명이 비어있습니다.", HttpStatus.BAD_REQUEST),
    INVALID_FILE_EXTENSION("잘못된 확장자명입니다.", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAILURE("파일 업로드 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILE_URL("잘못된 파일주소입니다.", HttpStatus.BAD_REQUEST),
    FILE_DELETE_FAILURE("파일 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    BALANCE_GAME_NOT_FOUND("밸런스 게임을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    RESULT_NOT_FOUND("밸런스 게임 결과를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;
}
