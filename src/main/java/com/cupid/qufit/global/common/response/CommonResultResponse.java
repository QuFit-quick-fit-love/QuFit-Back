package com.cupid.qufit.global.common.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommonResultResponse {
    private final Boolean isSuccess;
    private final String message;
}
