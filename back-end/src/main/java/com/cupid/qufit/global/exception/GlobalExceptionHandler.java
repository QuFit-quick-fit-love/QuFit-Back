package com.cupid.qufit.global.exception;


import com.cupid.qufit.global.common.response.FieldValidationExceptionResponse;
import com.cupid.qufit.global.exception.exceptionType.ChatException;
import com.cupid.qufit.global.exception.exceptionType.MemberException;
import com.cupid.qufit.global.exception.exceptionType.S3Exception;
import com.cupid.qufit.global.exception.exceptionType.TagException;
import com.cupid.qufit.global.exception.exceptionType.VideoException;
import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
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

    @ExceptionHandler(ChatException.class)
    public ResponseEntity<ErrorResponse> handleChatException(ChatException e) {
        log.debug("[ChatException] : {} is occurred", e.getErrorCode());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(VideoException.class)
    public ResponseEntity<ErrorResponse> handleChatException(VideoException e) {
        log.debug("[VideoException] : {} is occurred", e.getErrorCode());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("예상치 못한 오류 : [UnexpectedException] : ", e);
        return ErrorResponse.toResponseEntity(ErrorCode.UNEXPECTED_ERROR);
    }

    /*
    * * RequestBody dto에 대한 validation 적용 후 예외 처리
    * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<FieldValidationExceptionResponse>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        log.error("field validation error : [MethodArgumentNotValidException] : ", e);
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        List<FieldValidationExceptionResponse> errorResponse
                = fieldErrors.stream()
                             .map(error -> FieldValidationExceptionResponse.builder()
                                                                           .field(error.getField())
                                                                           .rejectedValue(error.getRejectedValue())
                                                                           .errorMessage(error.getDefaultMessage())
                                                                           .build())
                             .toList();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<ErrorResponse> handleS3Exception(S3Exception e) {
        log.debug("[S3Exception] : {} is occurred", e.getErrorCode());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    /*
     * * RequestParam dto에 대한 validation 적용 후 예외 처리
     * */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<?> handlResponseStatusException(
            HandlerMethodValidationException e) {
        List<ParameterValidationResult> fieldErrors = e.getValueResults();
        log.error("parameter validation error  : "+ e.getValueResults());

        List<FieldValidationExceptionResponse> errorResponse
                = fieldErrors.stream()
                             .map(error -> {
                                 return FieldValidationExceptionResponse.builder()
                                                                 .field(error.getMethodParameter()
                                                                             .getParameterName())
                                                                 .rejectedValue((String) error.getArgument())
                                                                 .errorMessage(error.getResolvableErrors().get(0).getDefaultMessage())
                                                                 .build();
                             })
                             .toList();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
