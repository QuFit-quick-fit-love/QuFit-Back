package com.cupid.qufit.global.exception.exceptionType;

import com.cupid.qufit.global.exception.CustomException;
import com.cupid.qufit.global.exception.ErrorCode;

public class VideoException extends CustomException {
    public VideoException(ErrorCode errorCode) {
        super(errorCode);
    }
}
