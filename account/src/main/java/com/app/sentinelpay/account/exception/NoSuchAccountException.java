package com.app.sentinelpay.account.exception;

import lombok.Getter;

@Getter
public class NoSuchAccountException extends RuntimeException {

    private final String errorCode = "NO_SUCH_ACCOUNT";

    private final String accountNumber;

    public NoSuchAccountException(String accountNumber) {
        super(String.format("No account found with account number: %s", accountNumber));
        this.accountNumber = accountNumber;
    }

}
