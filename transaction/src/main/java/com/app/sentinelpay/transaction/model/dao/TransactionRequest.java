package com.app.sentinelpay.transaction.model.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionRequest(
        @JsonProperty("sender_account_number")
        String senderAccountNumber,
        @JsonProperty("receiver_account_number")
        String receiverAccountNumber,
        @NotNull(message = "Transaction amount cannot be null")
        BigDecimal amount
) {
}
