package com.app.sentinelpay.transaction.service;

import com.app.sentinelpay.transaction.model.Transaction;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public interface TransactionService {

    String transfer(String senderAccountNumber, String receiverAccountNumber, BigDecimal amount) throws InterruptedException;

}
