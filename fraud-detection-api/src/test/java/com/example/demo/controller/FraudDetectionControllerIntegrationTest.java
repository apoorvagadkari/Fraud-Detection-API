package com.example.demo.controller;

import com.example.demo.model.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the FraudDetectionController.
 * Tests the full API endpoint with real components.
 */
@SpringBootTest
@AutoConfigureMockMvc
class FraudDetectionControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void shouldReturnFraudScore_ForValidRequest() throws Exception {
        // Arrange: Create a valid transaction request
        TransactionRequest request = createNormalTransaction();
        
        // Act & Assert
        mockMvc.perform(post("/api/score-transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.signals").isArray())
                .andExpect(jsonPath("$.signals", hasSize(4)))
                .andExpect(jsonPath("$.signals[*].signal", 
                        containsInAnyOrder("location", "ipAddress", "transaction", "cardDetails")));
    }
    
    @Test
    void shouldDetectFraud_ForFraudulentTransaction() throws Exception {
        // Arrange: Create a fraudulent transaction
        TransactionRequest request = createFraudulentTransaction();
        
        // Act & Assert
        mockMvc.perform(post("/api/score-transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.signals").isArray())
                .andExpect(jsonPath("$.signals[?(@.signal=='location')].potentialFraud").value(true))
                .andExpect(jsonPath("$.signals[?(@.signal=='ipAddress')].potentialFraud").value(true))
                .andExpect(jsonPath("$.signals[?(@.signal=='transaction')].potentialFraud").value(true))
                .andExpect(jsonPath("$.signals[?(@.signal=='cardDetails')].potentialFraud").value(true));
    }
    
    @Test
    void shouldReturnBadRequest_WhenCustomerNameMissing() throws Exception {
        // Arrange: Request with missing customer name
        String invalidJson = "{\"ipAddress\":\"8.8.8.8\",\"location\":{\"city\":\"Boston\",\"state\":\"MA\"}," +
                "\"paymentDetails\":{\"cardLast4\":\"1234\",\"nameOnCard\":\"John\",\"cardAmount\":100.0}," +
                "\"transactionDetails\":{\"merchantName\":\"Store\",\"merchantLocation\":{\"city\":\"Boston\",\"state\":\"MA\"},\"purchasedItemCount\":1}}";
        
        // Act & Assert
        mockMvc.perform(post("/api/score-transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldReturnBadRequest_WhenAmountIsNegative() throws Exception {
        // Arrange: Request with negative amount
        TransactionRequest request = createNormalTransaction();
        request.getPaymentDetails().setCardAmount(-50.0);
        
        // Act & Assert
        mockMvc.perform(post("/api/score-transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldReturnBadRequest_WhenItemCountIsNegative() throws Exception {
        // Arrange: Request with negative item count
        TransactionRequest request = createNormalTransaction();
        request.getTransactionDetails().setPurchasedItemCount(-1);
        
        // Act & Assert
        mockMvc.perform(post("/api/score-transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldReturnBadRequest_ForInvalidJson() throws Exception {
        // Arrange: Malformed JSON
        String invalidJson = "{invalid json}";
        
        // Act & Assert
        mockMvc.perform(post("/api/score-transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.messages").isArray());
    }
    
    @Test
    void shouldIncludeDetailsInResponse() throws Exception {
        // Arrange
        TransactionRequest request = createNormalTransaction();
        
        // Act & Assert
        mockMvc.perform(post("/api/score-transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.signals[0].details").isArray())
                .andExpect(jsonPath("$.signals[0].details", not(empty())));
    }
    
    @Test
    void shouldNotDetectFraud_WhenAllSignalsAreClean() throws Exception {
        // Arrange: Perfect legitimate transaction
        TransactionRequest request = createNormalTransaction();
        
        // Act & Assert
        mockMvc.perform(post("/api/score-transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.signals[?(@.signal=='location')].potentialFraud").value(false))
                .andExpect(jsonPath("$.signals[?(@.signal=='transaction')].potentialFraud").value(false))
                .andExpect(jsonPath("$.signals[?(@.signal=='cardDetails')].potentialFraud").value(false));
    }
    
    // Helper methods
    private TransactionRequest createNormalTransaction() {
        Location location = new Location("Boston", "MA");
        PaymentDetails paymentDetails = new PaymentDetails("4567", "John Smith", 50.0);
        TransactionDetails transactionDetails = new TransactionDetails(
                "Normal Store", location, 2);
        
        return new TransactionRequest(
                "John Smith",
                "8.8.8.8",
                location,
                paymentDetails,
                transactionDetails
        );
    }
    
    private TransactionRequest createFraudulentTransaction() {
        Location customerLocation = new Location("Boston", "MA");
        Location merchantLocation = new Location("Los Angeles", "CA");
        PaymentDetails paymentDetails = new PaymentDetails("1111", "Jane Doe", 1500.0);
        TransactionDetails transactionDetails = new TransactionDetails(
                "Electronics Store", merchantLocation, 15);
        
        return new TransactionRequest(
                "John Smith",
                "192.168.1.100",
                customerLocation,
                paymentDetails,
                transactionDetails
        );
    }
}
