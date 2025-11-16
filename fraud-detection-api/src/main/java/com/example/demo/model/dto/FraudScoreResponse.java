package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO containing all fraud signals for a transaction.
 * This is what the API returns to the merchant.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudScoreResponse {
    
    /**
     * List of all fraud signals evaluated for this transaction
     */
    private List<FraudSignal> signals;
}
