package com.app.sentinelpay.transaction.exception;

import com.app.sentinelpay.common.exception.DomainException;

import java.util.Map;

public class InvalidTransactionException extends DomainException {

    public InvalidTransactionException(String transactionId) {
        super(String.format("Transaction id: %s is invalid. Try again with a valid id.", transactionId));

        String errorCode = "INVALID_TRANSACTION_ID";
        Map<String, String> errorData = Map.of("transaction_id", transactionId);
        this.addExceptionInformation(errorCode, this.getMessage(), errorData);
    }

}
