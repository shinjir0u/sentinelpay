package com.app.sentinelpay.common.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public abstract class DomainException extends RuntimeException {

    private final String code;

    private final String message;

    private final Map<String, String> errorData;

    protected DomainException(String code, String message, Map<String, String> errorData) {
        super(message);
        this.code = code;
        this.message = message;
        this.errorData = errorData;
    }

}
