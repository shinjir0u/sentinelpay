package com.app.sentinelpay.common.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public abstract class DomainException extends RuntimeException {

    private String code;

    private String message;

    private Map<String, String> errorData;

    protected DomainException(String message) {
        super(message);
    }

    public void addExceptionInformation(String code, String message, Map<String, String> errorData) {
        this.code = code;
        this.message = message;
        this.errorData = errorData;
    }


}
