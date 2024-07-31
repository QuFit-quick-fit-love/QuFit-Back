package com.cupid.qufit.global.common.response;

import com.cupid.qufit.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
* * DTO validation 검증에서 발생한 오류를 반환하는 dto
* */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "validation 검증에서 발생한 오류를 반환하는 객체 (상태 코드 : 400)")
public class FieldValidationExceptionResponse {
    @Schema(description = "오류 발생한 필드명")
    private String field;
    @Schema(description = "오류 발생한 값")
    private Object rejectedValue;
    @Schema(description = "오류 메시지")
    private String errorMessage;
}
