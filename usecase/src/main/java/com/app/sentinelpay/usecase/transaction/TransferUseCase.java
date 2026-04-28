package com.app.sentinelpay.usecase.transaction;

import com.app.sentinelpay.idempotencyKey.model.IdempotencyKey;
import com.app.sentinelpay.idempotencyKey.service.IdempotencyKeyService;
import com.app.sentinelpay.transaction.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
@Transactional
public class TransferUseCase {

    private final TransactionService transactionService;

    private final IdempotencyKeyService idempotencyKeyService;

    public String initializeTransfer(String idempotencyKey, String senderAccountNumber, String receiverAccountNumber, BigDecimal amount) throws InterruptedException {

        IdempotencyKey processedKey = idempotencyKeyService.processExistingIdempotencyKey(idempotencyKey);

        if (processedKey != null)
            return processedKey.getResponse();

        return transactionService.initializeTransaction(senderAccountNumber, receiverAccountNumber, amount);

    }

    public String finalizeTransfer(String idempotencyKey, String transactionId) throws InterruptedException {

        IdempotencyKey processedKey = idempotencyKeyService.processExistingIdempotencyKey(idempotencyKey);

        if (processedKey != null)
            return processedKey.getResponse();

        return transactionService.finalizeTransaction(transactionId);

    }

}
