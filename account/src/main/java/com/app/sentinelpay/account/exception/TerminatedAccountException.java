package com.app.sentinelpay.account.exception;

import lombok.Getter;

@Getter
public class TerminatedAccountException extends RuntimeException {

    private final String errorCode = "TERMINATED_ACCOUNT";

    private final String accountNumber;

    public TerminatedAccountException(String accountNumber) {
        super(String.format("Transaction is not allowed as account number: %s is terminated", accountNumber));
        this.accountNumber = accountNumber;
    }

}
