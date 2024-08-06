package com.cupid.qufit.global.exception.exceptionType;

import com.cupid.qufit.global.exception.CustomException;
import com.cupid.qufit.global.exception.ErrorCode;

public class FriendException extends CustomException {

    public FriendException(ErrorCode errorCode) {
        super(errorCode);
    }
}
