package com.cupid.qufit.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "상태코드")
    private int status;
    @Schema(description = "에러 코드명")
    private ErrorCode errorCode;
    @Schema(description = "에러 메시지")
    private String errorMessage;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode e) {
        return ResponseEntity.status(e.getHttpStatus())
                             .body(ErrorResponse.builder()
                                                .status(e.getHttpStatus().value())
                                                .errorCode(e)
                                                .errorMessage(e.getMessage()).build());
    }
}
