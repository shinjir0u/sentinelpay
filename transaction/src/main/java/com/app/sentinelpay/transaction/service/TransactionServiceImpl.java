package com.app.sentinelpay.transaction.service;

import com.app.sentinelpay.account.model.Account;
import com.app.sentinelpay.account.repository.AccountRepository;
import com.app.sentinelpay.idempotencyKey.model.IdempotencyKey;
import com.app.sentinelpay.idempotencyKey.repository.IdempotencyKeyRepository;
import com.app.sentinelpay.transaction.model.Transaction;
import com.app.sentinelpay.transaction.model.type.TransactionStatus;
import com.app.sentinelpay.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final IdempotencyKeyRepository idempotencyKeyRepository;

    private final TaskScheduler taskScheduler;

    @Override
    public String transfer(String idempotencyKey, String senderAccountNumber, String receiverAccountNumber, BigDecimal amount) throws InterruptedException {

        Optional<IdempotencyKey> optionalIdempotencyKey = idempotencyKeyRepository.findById(idempotencyKey);

        if (optionalIdempotencyKey.isPresent()) {
            IdempotencyKey key = optionalIdempotencyKey.get();

            if (key.getExpiryDate().isBefore(Instant.now()))
                idempotencyKeyRepository.delete(key);
            else
                return key.getResponse();
        }

        Account senderAccount = accountRepository.findByAccountNumber(senderAccountNumber).orElseThrow();
        Account receiverAccount = accountRepository.findByAccountNumber(receiverAccountNumber).orElseThrow();

        if (senderAccount.isAccountTerminated() || receiverAccount.isAccountTerminated())
            throw new RuntimeException("Transaction from or to an terminated account is invalid");

        if (senderAccount.hasInsufficientBalance(amount))
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

        IdempotencyKey processedIdempotencyKey = IdempotencyKey.builder()
                                                    .key(idempotencyKey)
                                                    .response(savedTransaction.getId().toString())
                                                    .expiryDate(Instant.now().plus(24, ChronoUnit.HOURS))
                                                    .build();

        idempotencyKeyRepository.save(processedIdempotencyKey);

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

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

}
