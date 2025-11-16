package com.example.demo.domain;

import com.example.demo.model.dto.FraudSignal;
import com.example.demo.model.dto.TransactionRequest;
import com.example.demo.service.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates location-based fraud signals.
 * Checks if customer location matches merchant location and purchase history.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LocationSignalEvaluator {
    
    private final TransactionHistoryService historyService;
    
    public FraudSignal evaluate(TransactionRequest request) {
        log.debug("Evaluating location signal");
        
        List<String> details = new ArrayList<>();
        boolean potentialFraud = false;
        
        String customerName = request.getCustomerName();
        String customerCity = request.getLocation().getCity();
        String customerState = request.getLocation().getState();
        String merchantCity = request.getTransactionDetails().getMerchantLocation().getCity();
        String merchantState = request.getTransactionDetails().getMerchantLocation().getState();
        
        // Check if customer and merchant are in same location
        if (!customerCity.equalsIgnoreCase(merchantCity) || 
            !customerState.equalsIgnoreCase(merchantState)) {
            potentialFraud = true;
            details.add("Customer location differs from merchant location");
            details.add(String.format("Customer: %s, %s | Merchant: %s, %s", 
                    customerCity, customerState, merchantCity, merchantState));
        } else {
            details.add("Customer and merchant are in the same location");
        }
        
        // Check against historical purchase locations
        boolean hasVisitedBefore = historyService.hasVisitedLocation(
                customerName, merchantCity, merchantState);
        
        if (!hasVisitedBefore) {
            potentialFraud = true;
            details.add(String.format("New location for customer: %s, %s", merchantCity, merchantState));
            log.info("New location detected for customer {}: {}, {}", 
                    customerName, merchantCity, merchantState);
        } else {
            details.add("Customer has purchased from this location before");
        }
        
        return new FraudSignal("location", potentialFraud, details);
    }
}
