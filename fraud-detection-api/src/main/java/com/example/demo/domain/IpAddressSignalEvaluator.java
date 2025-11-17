package com.example.demo.domain;

import com.example.demo.model.dto.FraudSignal;
import com.example.demo.model.dto.TransactionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Evaluates IP address-based fraud signals.
 * Checks if the transaction's IP address is on a blacklist of known fraudulent IPs or comes from a private network (VPN/proxy), flagging it as suspicious if either is true.
 */
@Component
@Slf4j
public class IpAddressSignalEvaluator {
    
    // Simple blacklist for demo purposes
    // In production, this would be a database or external service
    private static final Set<String> KNOWN_FRAUDULENT_IPS = new HashSet<>(Arrays.asList(
        "192.168.1.100",
        "10.0.0.50",
        "172.16.0.200"
    ));
    
    public FraudSignal evaluate(TransactionRequest request) {
        log.debug("Evaluating IP address signal");
        
        List<String> details = new ArrayList<>();
        boolean potentialFraud = false;
        String ipAddress = request.getIpAddress();
        
        // Check against blacklist
        if (KNOWN_FRAUDULENT_IPS.contains(ipAddress)) {
            potentialFraud = true;
            details.add("IP address is on the known fraudulent list");
            details.add(String.format("Flagged IP: %s", ipAddress));
        } else {
            details.add("IP address is not known to be fraudulent or malicious");
        }
        
        // Check for private IP addresses (potential VPN/proxy)
        if (ipAddress.startsWith("10.") || ipAddress.startsWith("192.168.") || 
            ipAddress.startsWith("172.")) {
            details.add("IP address appears to be from a private network (potential VPN/proxy)");
        }
        
        return new FraudSignal("ipAddress", potentialFraud, details);
    }
}
