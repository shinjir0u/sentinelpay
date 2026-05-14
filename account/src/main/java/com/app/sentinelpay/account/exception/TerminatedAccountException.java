package com.app.sentinelpay.account.exception;

import com.app.sentinelpay.common.exception.DomainException;
import lombok.Getter;

import java.util.Map;

@Getter
public class TerminatedAccountException extends DomainException {

    public TerminatedAccountException(String accountNumber) {
        super(String.format("Transaction is not allowed as account number: %s is terminated", accountNumber));

        String code = "TERMINATED_ACCOUNT";
        Map<String, String> errorData = Map.of("account_number", accountNumber);
        this.addExceptionInformation(code, this.getMessage(), errorData);
    }

}
