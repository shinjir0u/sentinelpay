package com.app.sentinelpay.account.exception;

import lombok.Getter;

@Getter
public class InvalidAmountException extends RuntimeException {

    private final String errorCode = "INVALID_AMOUNT";

    private final String amount;

    public InvalidAmountException(String amount) {
        super(String.format("Amount of %s is invalid for amount must be greater than 0 and have at most 2 decimal places.", amount));
        this.amount = amount;
    }

}
