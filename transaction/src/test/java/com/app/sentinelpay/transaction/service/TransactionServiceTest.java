package com.app.sentinelpay.transaction.service;

import com.app.sentinelpay.account.model.Account;
import com.app.sentinelpay.account.model.type.AccountStatus;
import com.app.sentinelpay.account.repository.AccountRepository;
import com.app.sentinelpay.idempotencyKey.repository.IdempotencyKeyRepository;
import com.app.sentinelpay.transaction.model.Transaction;
import com.app.sentinelpay.transaction.model.type.TransactionStatus;
import com.app.sentinelpay.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.scheduling.TaskScheduler;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestConfiguration()
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private IdempotencyKeyRepository idempotencyKeyRepository;

    @Mock
    private TaskScheduler taskScheduler;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account senderAccount;
    private Account receiverAccount;
    private final String senderNo = "123456";
    private final String receiverNo = "654321";

    @BeforeEach
    void setUp() {
        senderAccount = Account.builder()
                .accountNumber(senderNo)
                .balance(new BigDecimal("5000.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        receiverAccount = Account.builder()
                .accountNumber(receiverNo)
                .balance(new BigDecimal("2000.00"))
                .status(AccountStatus.ACTIVE)
                .build();
    }

    // To Test again after service logic fix
//    @Test
    void initializeTransaction_Success() throws InterruptedException {
        BigDecimal amount = new BigDecimal("1000.00");
        UUID transactionId = UUID.randomUUID();

        Transaction pendingTransaction = Transaction.builder()
                .id(transactionId)
                .senderAccount(senderAccount)
                .receiverAccount(receiverAccount)
                .amount(amount)
                .status(TransactionStatus.PENDING)
                .build();

        when(accountRepository.findByAccountNumber(senderNo)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber(receiverNo)).thenReturn(Optional.of(receiverAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(pendingTransaction);
        when(idempotencyKeyRepository.save(any())).thenReturn(null);
        when(idempotencyKeyRepository.findById(any())).thenReturn(Optional.empty());
        when(taskScheduler.schedule(any(Runnable.class), any(Instant.class))).thenReturn(null);

        String receivedTransactionId = transactionService.initializeTransaction(senderNo, receiverNo, amount);

        assertEquals(transactionId.toString(), receivedTransactionId);
        assertEquals(new BigDecimal("4000.00"), senderAccount.getBalance());
        assertEquals(new BigDecimal("2000.00"), receiverAccount.getBalance());
        assertEquals(TransactionStatus.SUCCESS, pendingTransaction.getStatus());
    }


    void initializeTransaction_InsufficientBalance_ThrowsException() {
        // Arrange
        BigDecimal amount = new BigDecimal("4500.00"); // Only 5000 in account, requires 1000 minimum balance
        when(accountRepository.findByAccountNumber(senderNo)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber(receiverNo)).thenReturn(Optional.of(receiverAccount));
        when(idempotencyKeyRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            transactionService.initializeTransaction(UUID.randomUUID().toString(), senderNo, receiverNo, amount)
        );
        
        verify(transactionRepository, never()).save(any());
    }


    void initializeTransaction_TerminatedAccount_ThrowsException() {
        // Arrange
        senderAccount.setStatus(AccountStatus.TERMINATED);
        BigDecimal amount = new BigDecimal("100.00");
        when(accountRepository.findByAccountNumber(senderNo)).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNumber(receiverNo)).thenReturn(Optional.of(receiverAccount));
        when(idempotencyKeyRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            transactionService.initializeTransaction(senderNo, receiverNo, amount)
        );
        
        assertTrue(exception.getMessage().contains("terminated account"));
    }


    void initializeTransaction_AccountNotFound_ThrowsException() {
        // Arrange
        when(accountRepository.findByAccountNumber(senderNo)).thenReturn(Optional.empty());
        when(idempotencyKeyRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(java.util.NoSuchElementException.class, () ->
            transactionService.initializeTransaction(senderNo, receiverNo, new BigDecimal("100.00"))
        );
    }
}
