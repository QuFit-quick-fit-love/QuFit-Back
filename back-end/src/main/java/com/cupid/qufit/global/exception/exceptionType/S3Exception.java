package com.cupid.qufit.global.exception.exceptionType;

import com.cupid.qufit.global.exception.CustomException;
import com.cupid.qufit.global.exception.ErrorCode;

public class S3Exception extends CustomException {
    public S3Exception(ErrorCode errorCode) {
        super(errorCode);
    }
}

