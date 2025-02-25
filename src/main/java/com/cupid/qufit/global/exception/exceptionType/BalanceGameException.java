package com.cupid.qufit.global.exception.exceptionType;

import com.cupid.qufit.global.exception.CustomException;
import com.cupid.qufit.global.exception.ErrorCode;

public class BalanceGameException extends CustomException {

    public BalanceGameException(ErrorCode errorCode) {
        super(errorCode);
    }
}
