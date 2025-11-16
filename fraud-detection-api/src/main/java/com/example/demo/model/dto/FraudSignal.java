package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a single fraud signal in the response.
 * Each signal covers a different aspect of fraud detection.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudSignal {
    
    /**
     * Type of signal (e.g., "location", "ipAddress", "transaction", "cardDetails")
     */
    private String signal;
    
    /**
     * Whether this signal indicates potential fraud
     */
    private Boolean potentialFraud;
    
    /**
     * Detailed explanations for this signal's assessment
     */
    private List<String> details;
}
