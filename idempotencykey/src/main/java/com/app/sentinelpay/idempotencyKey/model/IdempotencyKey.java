package com.app.sentinelpay.idempotencyKey.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "idempotency_keys")
public class IdempotencyKey {

    @Id
    @Column(unique = true)
    private String key;

    private String response;

    private Instant expiryDate;

}
