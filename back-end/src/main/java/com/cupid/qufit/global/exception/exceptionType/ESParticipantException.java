package com.cupid.qufit.global.exception.exceptionType;

import com.cupid.qufit.global.exception.CustomException;
import com.cupid.qufit.global.exception.ErrorCode;

public class ESParticipantException extends CustomException {

    public ESParticipantException(ErrorCode errorCode) {
        super(errorCode);
    }
}
