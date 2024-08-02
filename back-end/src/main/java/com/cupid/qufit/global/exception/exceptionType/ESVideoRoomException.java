package com.cupid.qufit.global.exception.exceptionType;

import com.cupid.qufit.global.exception.CustomException;
import com.cupid.qufit.global.exception.ErrorCode;

public class ESVideoRoomException extends CustomException {

    public ESVideoRoomException(ErrorCode errorCode) {
        super(errorCode);
    }
}
