package com.app.sentinalpay.account.model;

import com.app.sentinalpay.account.model.type.AccountStatus;
import com.app.sentinalpay.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Version
    private Integer version;

}
