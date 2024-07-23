package com.cupid.qufit.global.exception.exceptionType;

import com.cupid.qufit.global.exception.CustomException;
import com.cupid.qufit.global.exception.ErrorCode;

public class CustomJWTExeption extends CustomException {

    public CustomJWTExeption(ErrorCode errorCode) {
        super(errorCode);
    }
}
