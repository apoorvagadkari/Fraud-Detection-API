package com.example.demo.domain;

import com.example.demo.model.dto.*;
import com.example.demo.service.TransactionHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for LocationSignalEvaluator.
 * Tests various location-based fraud scenarios.
 */
@ExtendWith(MockitoExtension.class)
class LocationSignalEvaluatorTest {
    
    @Mock
    private TransactionHistoryService historyService;
    
    private LocationSignalEvaluator evaluator;
    
    @BeforeEach
    void setUp() {
        evaluator = new LocationSignalEvaluator(historyService);
        
        // Default: Mock that customer has visited location before (to isolate location mismatch tests)
        when(historyService.hasVisitedLocation(anyString(), anyString(), anyString())).thenReturn(true);
    }
    
    @Test
    void shouldDetectFraud_WhenLocationsDiffer() {
        // Arrange: Create a transaction where customer and merchant are in different locations
        TransactionRequest request = createTransactionRequest(
                "Boston", "MA",
                "Los Angeles", "CA"
        );
        
        // Act: Evaluate the signal
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert: Should flag as potential fraud
        assertEquals("location", signal.getSignal());
        assertTrue(signal.getPotentialFraud(), "Should detect fraud when locations differ");
        assertTrue(signal.getDetails().stream()
                .anyMatch(detail -> detail.contains("Customer location differs from merchant location")));
    }
    
    @Test
    void shouldNotDetectFraud_WhenLocationsMatch() {
        // Arrange: Create a transaction where customer and merchant are in same location
        TransactionRequest request = createTransactionRequest(
                "Boston", "MA",
                "Boston", "MA"
        );
        
        // Act: Evaluate the signal
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert: Should NOT flag as fraud
        assertEquals("location", signal.getSignal());
        assertFalse(signal.getPotentialFraud(), "Should not detect fraud when locations match");
        assertTrue(signal.getDetails().stream()
                .anyMatch(detail -> detail.contains("Customer and merchant are in the same location")));
    }
    
    @Test
    void shouldDetectFraud_WhenCityMatchesButStateDiffers() {
        // Arrange: Same city name but different states
        TransactionRequest request = createTransactionRequest(
                "Springfield", "MA",
                "Springfield", "IL"
        );
        
        // Act: Evaluate the signal
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert: Should flag as fraud
        assertTrue(signal.getPotentialFraud(), "Should detect fraud when states differ");
    }
    
    @Test
    void shouldBeCaseInsensitive() {
        // Arrange: Different case but same location
        TransactionRequest request = createTransactionRequest(
                "BOSTON", "MA",
                "boston", "ma"
        );
        
        // Act: Evaluate the signal
        FraudSignal signal = evaluator.evaluate(request);
        
        // Assert: Should NOT flag as fraud (case insensitive)
        assertFalse(signal.getPotentialFraud(), "Should be case insensitive");
    }
    
    // Helper method to create test transaction requests
    private TransactionRequest createTransactionRequest(
            String customerCity, String customerState,
            String merchantCity, String merchantState) {
        
        Location customerLocation = new Location(customerCity, customerState);
        Location merchantLocation = new Location(merchantCity, merchantState);
        
        PaymentDetails paymentDetails = new PaymentDetails("1234", "John Doe", 100.0);
        TransactionDetails transactionDetails = new TransactionDetails(
                "Test Merchant", merchantLocation, 1);
        
        return new TransactionRequest(
                "John Doe",
                "192.168.1.1",
                customerLocation,
                paymentDetails,
                transactionDetails
        );
    }
}
