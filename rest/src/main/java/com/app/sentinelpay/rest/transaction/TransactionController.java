package com.app.sentinelpay.rest.transaction;

import com.app.sentinelpay.transaction.model.dao.TransactionRequest;
import com.app.sentinelpay.transaction.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<String> processTransaction(@RequestHeader("Idempotency-Key") String idempotencyKey, @RequestBody TransactionRequest transactionRequest) throws InterruptedException {
        String transactionId = transactionService.transfer(idempotencyKey, transactionRequest.senderAccountNumber(), transactionRequest.receiverAccountNumber(), transactionRequest.amount());
        return ResponseEntity.ok(transactionId);
    }

}
