package com.cupid.qufit.global.exception.exceptionType;

import com.cupid.qufit.global.exception.CustomException;
import com.cupid.qufit.global.exception.ErrorCode;

public class ChatException extends CustomException {

    public ChatException(ErrorCode errorCode) {
        super(errorCode);
    }
}
