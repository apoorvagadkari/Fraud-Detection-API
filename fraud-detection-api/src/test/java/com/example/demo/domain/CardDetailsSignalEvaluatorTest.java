package com.example.demo.domain;

import com.example.demo.model.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CardDetailsSignalEvaluator.
 * Tests card name matching and suspicious card patterns.
 */
class CardDetailsSignalEvaluatorTest {
    
    private CardDetailsSignalEvaluator evaluator;
    
    @BeforeEach
    void setUp() {
        evaluator = new CardDetailsSignalEvaluator();
    }
    
    @Test
    void shouldDetectFraud_WhenNamesDoNotMatch() {
        // Arrange: Customer name != name on card
        TransactionRequest request = createTransactionRequest("John Smith", "Jane Doe", "1234");
        
        // Act
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert
        assertEquals("cardDetails", signal.getSignal());
        assertTrue(signal.getPotentialFraud(), "Should detect fraud when names don't match");
        assertTrue(signal.getDetails().stream()
                .anyMatch(detail -> detail.contains("does not match")));
    }
    
    @Test
    void shouldNotDetectFraud_WhenNamesMatch() {
        // Arrange: Customer name == name on card
        TransactionRequest request = createTransactionRequest("John Smith", "John Smith", "1234");
        
        // Act
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert
        assertEquals("cardDetails", signal.getSignal());
        assertFalse(signal.getPotentialFraud(), "Should not detect fraud when names match");
        assertTrue(signal.getDetails().stream()
                .anyMatch(detail -> detail.contains("matches name on card")));
    }
    
    @Test
    void shouldBeCaseInsensitive_ForNameMatching() {
        // Arrange: Same name, different case
        TransactionRequest request = createTransactionRequest("JOHN SMITH", "john smith", "1234");
        
        // Act
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert
        assertFalse(signal.getPotentialFraud(), "Should be case insensitive");
    }
    
    @Test
    void shouldDetectFraud_WhenCardLast4AreAllSameDigit() {
        // Arrange: Suspicious pattern like "1111", "9999"
        TransactionRequest request = createTransactionRequest("John Smith", "John Smith", "1111");
        
        // Act
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert
        assertTrue(signal.getPotentialFraud(), "Should detect suspicious card pattern");
        assertTrue(signal.getDetails().stream()
                .anyMatch(detail -> detail.contains("suspicious pattern")));
    }
    
    @Test
    void shouldNotDetectFraud_ForNormalCardPattern() {
        // Arrange: Normal card last 4 digits
        TransactionRequest request = createTransactionRequest("John Smith", "John Smith", "4567");
        
        // Act
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert
        assertFalse(signal.getPotentialFraud(), "Should not flag normal card pattern");
    }
    
    @Test
    void shouldHandleExtraWhitespace_InNames() {
        // Arrange: Names with extra spaces
        TransactionRequest request = createTransactionRequest("  John Smith  ", "John Smith", "1234");
        
        // Act
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert
        assertFalse(signal.getPotentialFraud(), "Should handle whitespace correctly");
    }
    
    @Test
    void shouldDetectMultipleFraudIndicators() {
        // Arrange: Both name mismatch AND suspicious card pattern
        TransactionRequest request = createTransactionRequest("John Smith", "Jane Doe", "9999");
        
        // Act
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert
        assertTrue(signal.getPotentialFraud(), "Should detect multiple fraud indicators");
        assertTrue(signal.getDetails().size() >= 2, "Should have multiple fraud details");
    }
    
    // Helper method
    private TransactionRequest createTransactionRequest(
            String customerName, String nameOnCard, String cardLast4) {
        
        Location location = new Location("Boston", "MA");
        PaymentDetails paymentDetails = new PaymentDetails(cardLast4, nameOnCard, 100.0);
        TransactionDetails transactionDetails = new TransactionDetails(
                "Test Merchant", location, 1);
        
        return new TransactionRequest(
                customerName,
                "8.8.8.8",
                location,
                paymentDetails,
                transactionDetails
        );
    }
}
