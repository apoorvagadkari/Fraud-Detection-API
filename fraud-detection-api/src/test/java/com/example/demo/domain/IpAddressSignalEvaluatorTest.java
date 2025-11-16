package com.example.demo.domain;

import com.example.demo.model.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IpAddressSignalEvaluator.
 * Tests IP blacklist and VPN/proxy detection.
 */
class IpAddressSignalEvaluatorTest {
    
    private IpAddressSignalEvaluator evaluator;
    
    @BeforeEach
    void setUp() {
        evaluator = new IpAddressSignalEvaluator();
    }
    
    @Test
    void shouldDetectFraud_WhenIpIsBlacklisted() {
        // Arrange: Create transaction with a blacklisted IP
        TransactionRequest request = createTransactionRequest("192.168.1.100");
        
        // Act
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert
        assertEquals("ipAddress", signal.getSignal());
        assertTrue(signal.getPotentialFraud(), "Should detect fraud for blacklisted IP");
        assertTrue(signal.getDetails().stream()
                .anyMatch(detail -> detail.contains("known fraudulent list")));
    }
    
    @Test
    void shouldNotDetectFraud_WhenIpIsClean() {
        // Arrange: Use a non-blacklisted, non-private IP
        TransactionRequest request = createTransactionRequest("8.8.8.8");
        
        // Act
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert
        assertEquals("ipAddress", signal.getSignal());
        assertFalse(signal.getPotentialFraud(), "Should not detect fraud for clean IP");
        assertTrue(signal.getDetails().stream()
                .anyMatch(detail -> detail.contains("not known to be fraudulent")));
    }
    
    @Test
    void shouldDetectPrivateNetwork_StartingWith10() {
        // Arrange
        TransactionRequest request = createTransactionRequest("10.0.0.1");
        
        // Act
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert
        assertTrue(signal.getDetails().stream()
                .anyMatch(detail -> detail.contains("private network")),
                "Should detect private IP starting with 10.");
    }
    
    @Test
    void shouldDetectPrivateNetwork_StartingWith192() {
        // Arrange
        TransactionRequest request = createTransactionRequest("192.168.0.1");
        
        // Act
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert
        assertTrue(signal.getDetails().stream()
                .anyMatch(detail -> detail.contains("private network")),
                "Should detect private IP starting with 192.168");
    }
    
    @Test
    void shouldDetectPrivateNetwork_StartingWith172() {
        // Arrange
        TransactionRequest request = createTransactionRequest("172.16.0.1");
        
        // Act
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert
        assertTrue(signal.getDetails().stream()
                .anyMatch(detail -> detail.contains("private network")),
                "Should detect private IP starting with 172");
    }
    
    // Helper method
    private TransactionRequest createTransactionRequest(String ipAddress) {
        Location location = new Location("Boston", "MA");
        PaymentDetails paymentDetails = new PaymentDetails("1234", "John Doe", 100.0);
        TransactionDetails transactionDetails = new TransactionDetails(
                "Test Merchant", location, 1);
        
        return new TransactionRequest(
                "John Doe",
                ipAddress,
                location,
                paymentDetails,
                transactionDetails
        );
    }
}
