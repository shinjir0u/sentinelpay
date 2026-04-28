package com.app.sentinelpay.idempotencyKey.service;

import com.app.sentinelpay.idempotencyKey.model.IdempotencyKey;

public interface IdempotencyKeyService {

    IdempotencyKey processExistingIdempotencyKey(String idempotencyKey);

    IdempotencyKey saveKey(String idempotencyKey, String response);

}
