package com.app.sentinelpay.transaction.service;

import java.math.BigDecimal;

public interface TransactionService {

    String transfer(String idempotencyKey, String senderAccountNumber, String receiverAccountNumber, BigDecimal amount) throws InterruptedException;

}
