package com.app.sentinelpay.transaction.exception;

import lombok.Getter;

@Getter
public class NoSuchTransactionException extends RuntimeException {

    private final String errorCode = "NO_SUCH_TRANSACTION";

    private final String transactionId;

    public NoSuchTransactionException(String transactionId) {
        super(String.format("No transaction found with transaction: %s", transactionId));
        this.transactionId = transactionId;
    }

}
