package com.app.sentinelpay.account.service;

import com.app.sentinelpay.account.model.Account;
import com.app.sentinelpay.account.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public BigDecimal transfer(String senderAccountNumber, String receiverAccountNumber, BigDecimal amount) {
        Account senderAccount = accountRepository.findByAccountNumber(senderAccountNumber).orElseThrow();
        Account receiverAccount = accountRepository.findByAccountNumber(receiverAccountNumber).orElseThrow();

        if (senderAccount.isAccountTerminated() || receiverAccount.isAccountTerminated())
            throw new IllegalArgumentException("Transaction from or to an terminated account is invalid");

        if (senderAccount.hasInsufficientBalance(amount))
            throw new IllegalArgumentException("Insufficient balance: at least 1000 must remain in the account after transaction.");

        senderAccount.subtractBalance(amount);
        receiverAccount.addBalance(amount);

        return amount;
    }

}
