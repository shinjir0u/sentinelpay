package com.app.sentinelpay.common.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public abstract class DomainException extends RuntimeException {

    private String errorCode;

    private String errorMessage;

    private Map<String, String> errorData;

    protected DomainException(String message) {
        super(message);
    }

    public void addExceptionInformation(String errorCode, String errorMessage, Map<String, String> errorData) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorData = errorData;
    }

}
