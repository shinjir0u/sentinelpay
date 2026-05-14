package com.app.sentinelpay.account.exception;

import com.app.sentinelpay.common.exception.DomainException;
import lombok.Getter;

import java.util.Map;

@Getter
public class InvalidAmountException extends DomainException {

    public InvalidAmountException(String amount) {
        super(String.format("Amount of %s is invalid for amount must be greater than 0 and have at most 2 decimal places.", amount));

        String code = "INVALID_AMOUNT";
        Map<String, String> errorData = Map.of("amount", amount);
        this.addExceptionInformation(code, this.getMessage(), errorData);
    }

}
