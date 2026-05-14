package com.app.sentinelpay.account.exception;

import com.app.sentinelpay.common.exception.DomainException;
import lombok.Getter;

import java.util.Map;

@Getter
public class NoSuchAccountException extends DomainException {

    public NoSuchAccountException(String accountNumber) {
        super(String.format("No account found with account number: %s", accountNumber));

        String code = "NO_SUCH_ACCOUNT";
        Map<String, String> errorData = Map.of("account_number", accountNumber);
        this.addExceptionInformation(code, this.getMessage(), errorData);
    }

}
