package com.example.demo.domain;

import com.example.demo.model.dto.*;
import com.example.demo.service.TransactionHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for TransactionSignalEvaluator.
 * Tests transaction amount and item count thresholds.
 */
@ExtendWith(MockitoExtension.class)
class TransactionSignalEvaluatorTest {
    
    @Mock
    private TransactionHistoryService historyService;
    
    private TransactionSignalEvaluator evaluator;
    
    @BeforeEach
    void setUp() {
        evaluator = new TransactionSignalEvaluator(historyService);
        
        // Default: Mock normal velocity (0 recent transactions)
        when(historyService.countRecentTransactions(anyString(), any(LocalDateTime.class))).thenReturn(0L);
    }
    
    @Test
    void shouldDetectFraud_WhenAmountExceedsThreshold() {
        // Arrange: Create transaction with amount > $1000
        TransactionRequest request = createTransactionRequest(1500.0, 2);
        
        // Act
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert
        assertEquals("transaction", signal.getSignal());
        assertTrue(signal.getPotentialFraud(), "Should detect fraud for high amount");
        assertTrue(signal.getDetails().stream()
                .anyMatch(detail -> detail.contains("exceeds normal threshold")));
    }
    
    @Test
    void shouldDetectFraud_WhenItemCountExceedsThreshold() {
        // Arrange: Create transaction with > 10 items
        TransactionRequest request = createTransactionRequest(100.0, 15);
        
        // Act
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert
        assertEquals("transaction", signal.getSignal());
        assertTrue(signal.getPotentialFraud(), "Should detect fraud for high item count");
        assertTrue(signal.getDetails().stream()
                .anyMatch(detail -> detail.contains("unusually high")));
    }
    
    @Test
    void shouldNotDetectFraud_WhenWithinNormalRanges() {
        // Arrange: Normal transaction
        TransactionRequest request = createTransactionRequest(50.0, 2);
        
        // Act
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert
        assertEquals("transaction", signal.getSignal());
        assertFalse(signal.getPotentialFraud(), "Should not detect fraud for normal transaction");
        assertTrue(signal.getDetails().stream()
                .anyMatch(detail -> detail.contains("within normal ranges")));
    }
    
    @Test
    void shouldHandleBoundaryCase_JustOverThreshold() {
        // Arrange: Just over threshold
        TransactionRequest request = createTransactionRequest(1000.01, 11);
        
        // Act
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert: Should flag
        assertTrue(signal.getPotentialFraud(), "Should flag just over threshold");
    }
    
    // Helper method
    private TransactionRequest createTransactionRequest(double amount, int itemCount) {
        Location location = new Location("Boston", "MA");
        PaymentDetails paymentDetails = new PaymentDetails("1234", "John Doe", amount);
        TransactionDetails transactionDetails = new TransactionDetails(
                "Test Merchant", location, itemCount);
        
        return new TransactionRequest(
                "John Doe",
                "8.8.8.8",
                location,
                paymentDetails,
                transactionDetails
        );
    }
}
