package com.example.demo.domain;

import com.example.demo.model.dto.FraudSignal;
import com.example.demo.model.dto.TransactionRequest;
import com.example.demo.service.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates transaction-based fraud signals.
 * Checks for unusual transaction amounts, item counts, and patterns.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionSignalEvaluator {
    
    private static final double HIGH_AMOUNT_THRESHOLD = 1000.0;
    private static final int HIGH_ITEM_COUNT_THRESHOLD = 10;
    private static final int VELOCITY_THRESHOLD = 3; // More than 3 transactions
    private static final int VELOCITY_WINDOW_MINUTES = 10; // Within 10 minutes
    
    private final TransactionHistoryService historyService;
    
    public FraudSignal evaluate(TransactionRequest request) {
        log.debug("Evaluating transaction signal");
        
        List<String> details = new ArrayList<>();
        boolean potentialFraud = false;
        
        String customerName = request.getCustomerName();
        Double amount = request.getPaymentDetails().getCardAmount();
        Integer itemCount = request.getTransactionDetails().getPurchasedItemCount();
        
        // Check for unusually high transaction amount
        if (amount > HIGH_AMOUNT_THRESHOLD) {
            potentialFraud = true;
            details.add(String.format("Transaction amount ($%.2f) exceeds normal threshold ($%.2f)", 
                    amount, HIGH_AMOUNT_THRESHOLD));
        }
        
        // Check for unusually high item count
        if (itemCount > HIGH_ITEM_COUNT_THRESHOLD) {
            potentialFraud = true;
            details.add(String.format("Item count (%d) is unusually high (threshold: %d)", 
                    itemCount, HIGH_ITEM_COUNT_THRESHOLD));
        }
        
        // Calculate average price per item
        double avgPricePerItem = amount / itemCount;
        if (avgPricePerItem > 500) {
            details.add(String.format("High average price per item: $%.2f", avgPricePerItem));
        }
        
        // Velocity check - detect rapid succession of transactions
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(VELOCITY_WINDOW_MINUTES);
        long recentTransactionCount = historyService.countRecentTransactions(customerName, tenMinutesAgo);
        
        if (recentTransactionCount >= VELOCITY_THRESHOLD) {
            potentialFraud = true;
            details.add(String.format("Velocity alert: %d transactions in last %d minutes", 
                    recentTransactionCount, VELOCITY_WINDOW_MINUTES));
            log.warn("Velocity check failed for customer {}: {} transactions in {} minutes",
                    customerName, recentTransactionCount, VELOCITY_WINDOW_MINUTES);
        } else {
            details.add(String.format("Transaction velocity normal: %d transactions in last %d minutes",
                    recentTransactionCount, VELOCITY_WINDOW_MINUTES));
        }
        
        if (!potentialFraud) {
            details.add("Transaction amount and item count are within normal ranges");
        }
        
        return new FraudSignal("transaction", potentialFraud, details);
    }
}
