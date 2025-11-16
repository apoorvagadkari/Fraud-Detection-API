package com.example.demo.service;

import com.example.demo.domain.*;
import com.example.demo.model.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FraudDetectionService.
 * Uses mocks to isolate service logic from evaluators.
 */
@ExtendWith(MockitoExtension.class)
class FraudDetectionServiceTest {
    
    @Mock
    private LocationSignalEvaluator locationEvaluator;
    
    @Mock
    private IpAddressSignalEvaluator ipAddressEvaluator;
    
    @Mock
    private TransactionSignalEvaluator transactionEvaluator;
    
    @Mock
    private CardDetailsSignalEvaluator cardDetailsEvaluator;
    
    @Mock
    private TransactionHistoryService historyService;
    
    private FraudDetectionService service;
    
    @BeforeEach
    void setUp() {
        service = new FraudDetectionService(
                locationEvaluator,
                ipAddressEvaluator,
                transactionEvaluator,
                cardDetailsEvaluator,
                historyService
        );
    }
    
    @Test
    void shouldCallAllEvaluators() {
        // Arrange
        TransactionRequest request = createSampleRequest();
        
        // Mock the evaluators to return sample signals
        when(locationEvaluator.evaluate(any())).thenReturn(
                new FraudSignal("location", false, Arrays.asList("OK")));
        when(ipAddressEvaluator.evaluate(any())).thenReturn(
                new FraudSignal("ipAddress", false, Arrays.asList("OK")));
        when(transactionEvaluator.evaluate(any())).thenReturn(
                new FraudSignal("transaction", false, Arrays.asList("OK")));
        when(cardDetailsEvaluator.evaluate(any())).thenReturn(
                new FraudSignal("cardDetails", false, Arrays.asList("OK")));
        
        // Act
        FraudScoreResponse response = service.scoreTransaction(request);
        
        // Assert: Verify all evaluators were called exactly once
        verify(locationEvaluator, times(1)).evaluate(request);
        verify(ipAddressEvaluator, times(1)).evaluate(request);
        verify(transactionEvaluator, times(1)).evaluate(request);
        verify(cardDetailsEvaluator, times(1)).evaluate(request);
        
        // Verify transaction was saved to history
        verify(historyService, times(1)).saveTransaction(any());
    }
    
    @Test
    void shouldReturnAllFourSignals() {
        // Arrange
        TransactionRequest request = createSampleRequest();
        
        when(locationEvaluator.evaluate(any())).thenReturn(
                new FraudSignal("location", false, Arrays.asList("OK")));
        when(ipAddressEvaluator.evaluate(any())).thenReturn(
                new FraudSignal("ipAddress", false, Arrays.asList("OK")));
        when(transactionEvaluator.evaluate(any())).thenReturn(
                new FraudSignal("transaction", false, Arrays.asList("OK")));
        when(cardDetailsEvaluator.evaluate(any())).thenReturn(
                new FraudSignal("cardDetails", false, Arrays.asList("OK")));
        
        // Act
        FraudScoreResponse response = service.scoreTransaction(request);
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getSignals());
        assertEquals(4, response.getSignals().size(), "Should return exactly 4 signals");
    }
    
    @Test
    void shouldIncludeAllSignalTypes() {
        // Arrange
        TransactionRequest request = createSampleRequest();
        
        when(locationEvaluator.evaluate(any())).thenReturn(
                new FraudSignal("location", false, Arrays.asList("OK")));
        when(ipAddressEvaluator.evaluate(any())).thenReturn(
                new FraudSignal("ipAddress", false, Arrays.asList("OK")));
        when(transactionEvaluator.evaluate(any())).thenReturn(
                new FraudSignal("transaction", false, Arrays.asList("OK")));
        when(cardDetailsEvaluator.evaluate(any())).thenReturn(
                new FraudSignal("cardDetails", false, Arrays.asList("OK")));
        
        // Act
        FraudScoreResponse response = service.scoreTransaction(request);
        
        // Assert: Check all signal types are present
        assertTrue(response.getSignals().stream()
                .anyMatch(s -> s.getSignal().equals("location")));
        assertTrue(response.getSignals().stream()
                .anyMatch(s -> s.getSignal().equals("ipAddress")));
        assertTrue(response.getSignals().stream()
                .anyMatch(s -> s.getSignal().equals("transaction")));
        assertTrue(response.getSignals().stream()
                .anyMatch(s -> s.getSignal().equals("cardDetails")));
    }
    
    @Test
    void shouldHandleMixedFraudSignals() {
        // Arrange: Some signals detect fraud, others don't
        TransactionRequest request = createSampleRequest();
        
        when(locationEvaluator.evaluate(any())).thenReturn(
                new FraudSignal("location", true, Arrays.asList("Fraud detected")));
        when(ipAddressEvaluator.evaluate(any())).thenReturn(
                new FraudSignal("ipAddress", false, Arrays.asList("OK")));
        when(transactionEvaluator.evaluate(any())).thenReturn(
                new FraudSignal("transaction", true, Arrays.asList("High amount")));
        when(cardDetailsEvaluator.evaluate(any())).thenReturn(
                new FraudSignal("cardDetails", false, Arrays.asList("OK")));
        
        // Act
        FraudScoreResponse response = service.scoreTransaction(request);
        
        // Assert: Should contain both fraud and non-fraud signals
        long fraudCount = response.getSignals().stream()
                .filter(FraudSignal::getPotentialFraud)
                .count();
        assertEquals(2, fraudCount, "Should have 2 fraud signals");
    }
    
    // Helper method
    private TransactionRequest createSampleRequest() {
        Location location = new Location("Boston", "MA");
        PaymentDetails paymentDetails = new PaymentDetails("1234", "John Doe", 100.0);
        TransactionDetails transactionDetails = new TransactionDetails(
                "Test Merchant", location, 1);
        
        return new TransactionRequest(
                "John Doe",
                "8.8.8.8",
                location,
                paymentDetails,
                transactionDetails
        );
    }
}
