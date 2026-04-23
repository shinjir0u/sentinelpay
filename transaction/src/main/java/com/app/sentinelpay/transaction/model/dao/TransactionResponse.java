package com.app.sentinelpay.transaction.model.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransactionResponse(
        @JsonProperty("transaction_id")
        String transactionId
) {
}
