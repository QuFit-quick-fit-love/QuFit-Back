package com.cupid.qufit.global.exception.exceptionType;

import com.cupid.qufit.global.exception.CustomException;
import com.cupid.qufit.global.exception.ErrorCode;

public class ESIndexException extends CustomException {
    public ESIndexException(ErrorCode errorCode) {
        super(errorCode);
    }
}