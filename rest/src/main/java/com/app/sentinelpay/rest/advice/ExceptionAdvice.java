package com.app.sentinelpay.rest.advice;

import com.app.sentinelpay.common.exception.DomainException;
import lombok.Builder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice("com.app.sentinelpay.rest")
public class ExceptionAdvice {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(DomainException exception) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("code", exception.getErrorCode());
        errorMap.put("message", exception.getErrorMessage());

        Map<String, String> errors = new HashMap<>();
        errorMap.put("error_data", exception.getErrorData());
        return ResponseEntity.badRequest().body(errorMap);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUUIDException(HandlerMethodValidationException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getParameterValidationResults().forEach(result -> {
            String parameterName = result.getMethodParameter().getParameterName();
            result.getResolvableErrors().forEach(error -> {
                String message = error.getDefaultMessage();
                errors.put(parameterName, message);
            });
        });

        ErrorResponse response = ErrorResponse.builder()
                                    .code(exception.getStatusCode().toString())
                                    .message(exception.getReason())
                                    .errors(errors)
                                    .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleNullAmountException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });

        ErrorResponse response = ErrorResponse.builder()
                .code(exception.getStatusCode().toString())
                .message(exception.getBody().getDetail())
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @Builder
    public record ErrorResponse (
            String code,
            String message,
            Map<String, String> errors
    ){
    }

}
