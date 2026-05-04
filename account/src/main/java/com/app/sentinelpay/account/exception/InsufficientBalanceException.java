package com.app.sentinelpay.account.exception;

import lombok.Getter;

@Getter
public class InsufficientBalanceException extends RuntimeException {

    private final String errorCode = "INSUFFICIENT_BALANCE";

    private final String accountNumber;

    private final String amount;

    public InsufficientBalanceException(String accountNumber, String amount) {
        super(String.format("Failed to process transaction as account number: %s has insufficient balance for amount: %s", accountNumber, amount));
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

}
