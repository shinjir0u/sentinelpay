package com.app.sentinelpay.transaction.scheduler;

import com.app.sentinelpay.transaction.model.Transaction;
import com.app.sentinelpay.transaction.model.type.TransactionStatus;
import com.app.sentinelpay.transaction.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@AllArgsConstructor
public class TransactionScheduler {

    private final TransactionRepository transactionRepository;

    @Scheduled(cron = "0 */15 * * * *")
    public void failOverduePendingTransactions() {
        List<Transaction> overduePendingTransactions = transactionRepository.findAllByStatusAndUpdatedAtBefore(TransactionStatus.PENDING, Instant.now().minus(1, ChronoUnit.DAYS));
        overduePendingTransactions.forEach(transaction -> {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
        });
    }

}
