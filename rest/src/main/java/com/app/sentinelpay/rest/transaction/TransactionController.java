package com.app.sentinelpay.rest.transaction;

import com.app.sentinelpay.transaction.model.dao.TransactionRequest;
import com.app.sentinelpay.usecase.transaction.TransferUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransferUseCase transferUseCase;

    @PostMapping
    public ResponseEntity<String> processInitialTransaction(@RequestHeader("Idempotency-Key") String idempotencyKey, @RequestBody TransactionRequest transactionRequest) throws InterruptedException {
        String transactionId = transferUseCase.initializeTransfer(idempotencyKey, transactionRequest.senderAccountNumber(), transactionRequest.receiverAccountNumber(), transactionRequest.amount());
        return ResponseEntity.ok(transactionId);
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> processFinalTransaction(@RequestHeader("Idempotency-Key") String idempotencyKey, @PathVariable String id) throws InterruptedException {
        String transactionId = transferUseCase.finalizeTransfer(idempotencyKey, id);
        return ResponseEntity.ok(transactionId);
    }

}
