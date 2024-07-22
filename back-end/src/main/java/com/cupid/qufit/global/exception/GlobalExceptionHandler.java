package com.cupid.qufit.global.exception;


import com.cupid.qufit.global.exception.exceptionType.MemberException;
import com.cupid.qufit.global.exception.exceptionType.TagException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e){
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ErrorResponse> handleMemberException(MemberException e) {
        log.debug("[MemberException] : {} is occurred", e.getErrorCode());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(TagException.class)
    public ResponseEntity<ErrorResponse> handleTagException(TagException e) {
        log.debug("[TagException] : {} is occurred", e.getErrorCode());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }
}
