package com.app.sentinelpay.transaction.service;

import com.app.sentinelpay.account.exception.InsufficientBalanceException;
import com.app.sentinelpay.account.exception.NoSuchAccountException;
import com.app.sentinelpay.account.exception.TerminatedAccountException;
import com.app.sentinelpay.account.model.Account;
import com.app.sentinelpay.account.repository.AccountRepository;
import com.app.sentinelpay.idempotencyKey.repository.IdempotencyKeyRepository;
import com.app.sentinelpay.transaction.exception.NoSuchTransactionException;
import com.app.sentinelpay.transaction.model.Transaction;
import com.app.sentinelpay.transaction.model.type.TransactionStatus;
import com.app.sentinelpay.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final IdempotencyKeyRepository idempotencyKeyRepository;

    private final TaskScheduler taskScheduler;

    @Override
    public String initializeTransaction(String senderAccountNumber, String receiverAccountNumber, BigDecimal amount) {
        Account senderAccount = accountRepository.findByAccountNumber(senderAccountNumber)
                                                    .orElseThrow(() -> new NoSuchAccountException(senderAccountNumber));
        Account receiverAccount = accountRepository.findByAccountNumber(receiverAccountNumber)
                                                    .orElseThrow(() -> new NoSuchAccountException(receiverAccountNumber));

        if (senderAccount.isAccountTerminated())
            throw new TerminatedAccountException(senderAccount.getAccountNumber());

        if (receiverAccount.isAccountTerminated())
            throw new TerminatedAccountException(receiverAccount.getAccountNumber());

        if (senderAccount.hasInsufficientBalance(amount))
            throw new InsufficientBalanceException(senderAccount.getAccountNumber(), amount.toString());

        Transaction transaction = Transaction.builder()
                                    .senderAccount(senderAccount)
                                    .receiverAccount(receiverAccount)
                                    .amount(amount)
                                    .status(TransactionStatus.PENDING)
                                    .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        return savedTransaction.getId().toString();
    }

    @Override
    public String finalizeTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findById(UUID.fromString(transactionId))
                                                           .orElseThrow(() -> new NoSuchTransactionException(transactionId));

        String senderAccountNumber = transaction.getSenderAccount().getAccountNumber();
        String receiverAccountNumber = transaction.getReceiverAccount().getAccountNumber();
        BigDecimal transactionAmount = transaction.getAmount();

        Account senderAccount = accountRepository.findByAccountNumber(senderAccountNumber)
                                                     .orElseThrow(() -> new NoSuchAccountException(senderAccountNumber));
        Account receiverAccount = accountRepository.findByAccountNumber(receiverAccountNumber)
                                                    .orElseThrow(() -> new NoSuchAccountException(receiverAccountNumber));

        if (senderAccount.isAccountTerminated())
            throw new TerminatedAccountException(senderAccountNumber);

        if (receiverAccount.isAccountTerminated())
            throw new TerminatedAccountException(receiverAccountNumber);

        if (senderAccount.hasInsufficientBalance(transaction.getAmount()))
            throw new InsufficientBalanceException(senderAccountNumber, transactionAmount.toString());

        senderAccount.subtractBalance(transactionAmount);
        receiverAccount.addBalance(transactionAmount);

        Transaction successTransaction = transaction.toBuilder()
                                            .status(TransactionStatus.SUCCESS)
                                            .build();
        Transaction savedTransaction = transactionRepository.save(successTransaction);
        return savedTransaction.getId().toString();
    }

}
