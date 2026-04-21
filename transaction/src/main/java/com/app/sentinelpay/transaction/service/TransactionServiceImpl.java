package com.app.sentinelpay.transaction.service;

import com.app.sentinelpay.account.model.Account;
import com.app.sentinelpay.account.repository.AccountRepository;
import com.app.sentinelpay.transaction.model.Transaction;
import com.app.sentinelpay.transaction.model.type.TransactionStatus;
import com.app.sentinelpay.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    @Override
    public String transfer(String senderAccountId, String receiverAccountId, BigDecimal amount) {

        Account senderAccount = accountRepository.findByAccountNumber(senderAccountId).orElseThrow();
        Account receiverAccount = accountRepository.findByAccountNumber(receiverAccountId).orElseThrow();

        if (senderAccount.isAccountTerminated() || receiverAccount.isAccountTerminated())
            throw new RuntimeException("Transaction from or to an terminated account is invalid");

        senderAccount.subtractBalance(amount);
        receiverAccount.addBalance(amount);

        Transaction transaction = Transaction.builder()
                                    .senderAccount(senderAccount)
                                    .receiverAccount(receiverAccount)
                                    .amount(amount)
                                    .status(TransactionStatus.PENDING)
                                    .build();

        return transactionRepository.save(transaction).getId().toString();
    }

}
