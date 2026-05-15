package com.app.sentinelpay.transaction.exception;

import com.app.sentinelpay.common.exception.DomainException;
import lombok.Getter;

import java.util.Map;

@Getter
public class NoSuchTransactionException extends DomainException {

    public NoSuchTransactionException(String transactionId) {
        super(String.format("No transaction found with transaction: %s", transactionId));

        String errorCode = "NO_SUCH_TRANSACTION";
        Map<String, String> errorData = Map.of("transaction_id", transactionId);

        this.addExceptionInformation(errorCode, this.getMessage(), errorData);
    }

}
