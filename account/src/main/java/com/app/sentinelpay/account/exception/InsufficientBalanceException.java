package com.app.sentinelpay.account.exception;

import com.app.sentinelpay.common.exception.DomainException;
import lombok.Getter;

import java.util.Map;

@Getter
public class InsufficientBalanceException extends DomainException {

    public InsufficientBalanceException(String accountNumber, String amount) {
        super(String.format("Failed to process transaction as account number: %s has insufficient balance for amount: %s", accountNumber, amount));

        String code = "INSUFFICIENT_BALANCE";
        Map<String, String> errorData = Map.of("account_number", accountNumber, "amount", amount);

        this.addExceptionInformation(code, this.getMessage(), errorData);
    }

}
