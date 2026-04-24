package com.app.sentinelpay.transaction.service;

import com.app.sentinelpay.account.model.Account;
import com.app.sentinelpay.account.repository.AccountRepository;
import com.app.sentinelpay.transaction.model.Transaction;
import com.app.sentinelpay.transaction.model.type.TransactionStatus;
import com.app.sentinelpay.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@AllArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final TaskScheduler taskScheduler;

    @Override
    public String transfer(String senderAccountNumber, String receiverAccountNumber, BigDecimal amount) throws InterruptedException {

        Account senderAccount = accountRepository.findByAccountNumber(senderAccountNumber).orElseThrow();
        Account receiverAccount = accountRepository.findByAccountNumber(receiverAccountNumber).orElseThrow();

        if (senderAccount.isAccountTerminated() || receiverAccount.isAccountTerminated())
            throw new RuntimeException("Transaction from or to an terminated account is invalid");

        if (!senderAccount.hasInsufficientBalance(amount))
            throw new IllegalArgumentException("Insufficient balance: at least 1000 must remain in the account after transaction.");

        Transaction transaction = Transaction.builder()
                                    .senderAccount(senderAccount)
                                    .receiverAccount(receiverAccount)
                                    .amount(amount)
                                    .status(TransactionStatus.PENDING)
                                    .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        taskScheduler.schedule(() -> {
            try {
                finalizeTransaction(savedTransaction);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, Instant.now().plusSeconds(100));

        return savedTransaction.getId().toString();

    }

    private void finalizeTransaction(Transaction transaction) throws InterruptedException {
        Account senderAccount = accountRepository.findByAccountNumber(transaction.getSenderAccount().getAccountNumber()).orElseThrow();
        Account receiverAccount = accountRepository.findByAccountNumber(transaction.getReceiverAccount().getAccountNumber()).orElseThrow();

        if (senderAccount.isAccountTerminated() || receiverAccount.isAccountTerminated())
            throw new RuntimeException("Transaction from or to an terminated account is invalid");

        senderAccount.subtractBalance(transaction.getAmount());
        receiverAccount.addBalance(transaction.getAmount());

        Transaction successTransaction = transaction.toBuilder().status(TransactionStatus.SUCCESS).build();
        transactionRepository.save(successTransaction);
    }

}
