package com.cupid.qufit.global.exception.exceptionType;

import com.cupid.qufit.global.exception.CustomException;
import com.cupid.qufit.global.exception.ErrorCode;

public class TagException extends CustomException {
    public TagException(ErrorCode errorCode) {
        super(errorCode);
    }
}
