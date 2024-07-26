package com.cupid.qufit.global.common.response;

import com.cupid.qufit.global.exception.ErrorCode;
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
public class FieldValidationExceptionResponse {
    private String field;
    private Object rejectedValue;
    private String errorMessage;
}
