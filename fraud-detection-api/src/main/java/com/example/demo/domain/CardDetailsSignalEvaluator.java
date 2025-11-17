package com.example.demo.domain;

import com.example.demo.model.dto.FraudSignal;
import com.example.demo.model.dto.TransactionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates card-based fraud signals.
 * Checks if the customer's name matches the name on the card and detects suspicious card patterns (like "1111" or "9999"), flagging potential fraud if there's a mismatch or pattern.
 */
@Component
@Slf4j
public class CardDetailsSignalEvaluator {
    
    public FraudSignal evaluate(TransactionRequest request) {
        log.debug("Evaluating card details signal");
        
        List<String> details = new ArrayList<>();
        boolean potentialFraud = false;
        
        String customerName = request.getCustomerName().trim();
        String nameOnCard = request.getPaymentDetails().getNameOnCard().trim();
        
        // Check if customer name matches name on card
        if (!customerName.equalsIgnoreCase(nameOnCard)) {
            potentialFraud = true;
            details.add("Customer name does not match name on card");
            details.add(String.format("Customer: '%s' | Card: '%s'", customerName, nameOnCard));
        } else {
            details.add("Customer name matches name on card");
        }
        
        // Check card last 4 digits for patterns
        String cardLast4 = request.getPaymentDetails().getCardLast4();
        if (cardLast4.matches("(\\d)\\1{3}")) {
            // All same digits (e.g., "1111", "9999")
            details.add("Card last 4 digits show suspicious pattern (all same digits)");
            potentialFraud = true;
        }
        
        if (!potentialFraud) {
            details.add("Card details appear legitimate");
        }
        
        return new FraudSignal("cardDetails", potentialFraud, details);
    }
}
