package com.app.sentinelpay.idempotencyKey.service;

import com.app.sentinelpay.idempotencyKey.model.IdempotencyKey;
import com.app.sentinelpay.idempotencyKey.repository.IdempotencyKeyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@AllArgsConstructor
public class IdempotencyKeyServiceImpl implements IdempotencyKeyService {

    private final IdempotencyKeyRepository idempotencyKeyRepository;

    @Override
    public IdempotencyKey processExistingIdempotencyKey(String idempotencyKey) {
        Optional<IdempotencyKey> optionalIdempotencyKey = idempotencyKeyRepository.findById(idempotencyKey);

        if (optionalIdempotencyKey.isPresent()) {
            IdempotencyKey key = optionalIdempotencyKey.get();

            if (key.getExpiryDate().isBefore(Instant.now()))
                idempotencyKeyRepository.delete(key);
            else
                return key;
        }
        return null;
    }

    @Override
    public IdempotencyKey saveKey(String idempotencyKey, String response) {
        IdempotencyKey newKey = IdempotencyKey.builder()
                                .key(idempotencyKey)
                                .response(response)
                                .expiryDate(Instant.now().plus(15, ChronoUnit.MINUTES))
                                .build();
        return idempotencyKeyRepository.save(newKey);
    }

}
