package com.cupid.qufit.global.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@Builder
@ToString
public class ErrorResponse {
    private int status;
    private ErrorCode errorCode;
    private String errorMessage;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode e) {
        return ResponseEntity.status(e.getHttpStatus())
                             .body(ErrorResponse.builder()
                                                .status(e.getHttpStatus().value())
                                                .errorCode(e)
                                                .errorMessage(e.getMessage()).build());
    }
}
