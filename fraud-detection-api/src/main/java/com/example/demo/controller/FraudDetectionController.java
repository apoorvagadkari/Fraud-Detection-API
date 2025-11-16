package com.example.demo.controller;

import com.example.demo.model.dto.FraudScoreResponse;
import com.example.demo.model.dto.TransactionRequest;
import com.example.demo.service.FraudDetectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for the fraud detection API.
 * Handles incoming transaction scoring requests.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionController {
    
    private final FraudDetectionService fraudDetectionService;
    
    /**
     * Endpoint to score a transaction for fraud.
     * 
     * @param request The transaction details to score
     * @return FraudScoreResponse containing all fraud signals
     */
    @PostMapping("/score-transaction")
    public ResponseEntity<FraudScoreResponse> scoreTransaction(
            @Valid @RequestBody TransactionRequest request) {
        
        log.info("Received transaction scoring request for customer: {}", 
                request.getCustomerName());
        
        FraudScoreResponse response = fraudDetectionService.scoreTransaction(request);
        
        log.info("Completed fraud scoring with {} signals", 
                response.getSignals().size());
        
        return ResponseEntity.ok(response);
    }
}
