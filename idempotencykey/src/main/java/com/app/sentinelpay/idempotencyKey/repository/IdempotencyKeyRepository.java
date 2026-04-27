package com.app.sentinelpay.idempotencyKey.repository;

import com.app.sentinelpay.idempotencyKey.model.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String> {
}
