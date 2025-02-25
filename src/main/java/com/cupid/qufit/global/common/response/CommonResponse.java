package com.cupid.qufit.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(Include.NON_NULL)
public class CommonResponse<T> {

    private final T data;
    private final Boolean isSuccess;
    private final String message;
}
