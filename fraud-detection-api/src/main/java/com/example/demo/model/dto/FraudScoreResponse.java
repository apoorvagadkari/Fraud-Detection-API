package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO containing all fraud signals for a transaction.
 * This is the structure of the JSON response sent back to the merchant, containing a list of all fraud check results (location, IP, transaction, card details).
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
