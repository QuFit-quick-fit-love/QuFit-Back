package com.cupid.qufit.global.exception.exceptionType;

import com.cupid.qufit.global.exception.CustomException;
import com.cupid.qufit.global.exception.ErrorCode;

public class CustomJWTException extends CustomException {

    public CustomJWTException(ErrorCode errorCode) {
        super(errorCode);
    }
}
