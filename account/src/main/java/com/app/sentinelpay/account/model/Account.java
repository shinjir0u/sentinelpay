package com.app.sentinelpay.account.model;

import com.app.sentinelpay.account.exception.InsufficientBalanceException;
import com.app.sentinelpay.account.exception.InvalidAmountException;
import com.app.sentinelpay.account.model.type.AccountStatus;
import com.app.sentinelpay.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(precision = 19, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Version
    private Integer version;

    public boolean hasInsufficientBalance(BigDecimal amount) {
        return balance.subtract(amount).compareTo(BigDecimal.valueOf(1000)) < 0;
    }

    private boolean isInvalidAmount(BigDecimal amount) {
        return amount.scale() > 2 || amount.compareTo(BigDecimal.ZERO) <= 0;
    }

    public BigDecimal subtractBalance(BigDecimal amount) {
        if (isInvalidAmount(amount))
            throw new InvalidAmountException(amount.toString());

        if (hasInsufficientBalance(amount))
            throw new InsufficientBalanceException(accountNumber, amount.toString());

        return balance = balance.subtract(amount);
    }

    public BigDecimal addBalance(BigDecimal amount) {
        if (isInvalidAmount(amount))
            throw new InvalidAmountException(amount.toString());

        return balance = balance.add(amount);
    }

    public boolean isAccountTerminated() {
        return status.equals(AccountStatus.TERMINATED);
    }

}
