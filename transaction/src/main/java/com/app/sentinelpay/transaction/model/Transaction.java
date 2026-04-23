package com.app.sentinelpay.transaction.model;

import com.app.sentinelpay.account.model.Account;
import com.app.sentinelpay.transaction.model.type.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "transaction_start_date")
    @CreatedDate
    private Instant startDate;

    @Column(name = "transaction_end_date")
    @LastModifiedDate
    private Instant endDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sender_account_id")
    private Account senderAccount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "receiver_account_id")
    private Account receiverAccount;

}
