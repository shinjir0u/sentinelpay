package com.app.sentinelpay.rest.transaction;

import com.app.sentinelpay.transaction.model.dao.TransactionRequest;
import com.app.sentinelpay.transaction.model.dao.TransactionResponse;
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
    public ResponseEntity<TransactionResponse> processInitialTransaction(@RequestHeader("Idempotency-Key") String idempotencyKey, @RequestBody TransactionRequest transactionRequest) throws InterruptedException {
        String transactionId = transferUseCase.initializeTransfer(idempotencyKey, transactionRequest.senderAccountNumber(), transactionRequest.receiverAccountNumber(), transactionRequest.amount());
        return ResponseEntity.ok(new TransactionResponse(transactionId));
    }

    @PostMapping("/{id}")
    public ResponseEntity<TransactionResponse> processFinalTransaction(@RequestHeader("Idempotency-Key") String idempotencyKey, @PathVariable String id) throws InterruptedException {
        String transactionId = transferUseCase.finalizeTransfer(idempotencyKey, id);
        return ResponseEntity.ok(new TransactionResponse(transactionId));
    }

}
