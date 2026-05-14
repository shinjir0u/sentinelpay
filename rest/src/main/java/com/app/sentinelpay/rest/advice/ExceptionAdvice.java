package com.app.sentinelpay.rest.advice;

import com.app.sentinelpay.common.exception.DomainException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice("com.app.sentinelpay.rest")
public class ExceptionAdvice {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(DomainException exception) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("code", exception.getCode());
        errorMap.put("message", exception.getMessage());
        errorMap.put("data", exception.getErrorData());
        return ResponseEntity.badRequest().body(errorMap);
    }

}
