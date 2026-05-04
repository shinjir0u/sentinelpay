package com.app.sentinelpay.transaction.service;

import java.math.BigDecimal;

public interface TransactionService {

    String initializeTransaction(String senderAccountNumber, String receiverAccountNumber, BigDecimal amount);

    String finalizeTransaction(String transactionId);

}
