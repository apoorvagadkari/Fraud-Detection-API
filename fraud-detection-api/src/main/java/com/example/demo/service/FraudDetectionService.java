package com.example.demo.service;

import com.example.demo.domain.*;
import com.example.demo.model.dto.FraudScoreResponse;
import com.example.demo.model.dto.FraudSignal;
import com.example.demo.model.dto.TransactionRequest;
import com.example.demo.model.entity.TransactionRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service that orchestrates fraud detection across multiple signal evaluators.
 * This is the main business logic coordinator.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {
    
    private final LocationSignalEvaluator locationEvaluator;
    private final IpAddressSignalEvaluator ipAddressEvaluator;
    private final TransactionSignalEvaluator transactionEvaluator;
    private final CardDetailsSignalEvaluator cardDetailsEvaluator;
    private final TransactionHistoryService historyService;
    
    /**
     * Scores a transaction by running it through all fraud signal evaluators.
     * Also saves the transaction to history for future analysis.
     * 
     * @param request The transaction to evaluate
     * @return FraudScoreResponse with all signals
     */
    public FraudScoreResponse scoreTransaction(TransactionRequest request) {
        log.debug("Starting fraud detection for transaction");
        
        List<FraudSignal> signals = new ArrayList<>();
        
        // Evaluate each fraud signal independently
        signals.add(locationEvaluator.evaluate(request));
        signals.add(ipAddressEvaluator.evaluate(request));
        signals.add(transactionEvaluator.evaluate(request));
        signals.add(cardDetailsEvaluator.evaluate(request));
        
        // Save transaction to history for future fraud detection
        saveTransactionToHistory(request);
        
        log.debug("Fraud detection complete. Generated {} signals", signals.size());
        
        return new FraudScoreResponse(signals);
    }
    
    /**
     * Saves the transaction to history for future analysis.
     */
    private void saveTransactionToHistory(TransactionRequest request) {
        TransactionRecord record = new TransactionRecord(
                request.getCustomerName(),
                request.getTransactionDetails().getMerchantLocation().getCity(),
                request.getTransactionDetails().getMerchantLocation().getState(),
                request.getPaymentDetails().getCardAmount(),
                LocalDateTime.now(),
                request.getIpAddress(),
                request.getTransactionDetails().getMerchantName()
        );
        
        historyService.saveTransaction(record);
        log.debug("Saved transaction to history for customer: {}", record.getCustomerName());
    }
}
