package com.example.demo.service;

import com.example.demo.model.entity.TransactionRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service for managing transaction history.
 * Uses in-memory storage for simplicity (production would use a database).
 */
@Service
@Slf4j
public class TransactionHistoryService {
    
    // In-memory storage: customerName -> list of transactions
    private final Map<String, List<TransactionRecord>> transactionHistory = new ConcurrentHashMap<>();
    
    /**
     * Save a transaction to history.
     */
    public void saveTransaction(TransactionRecord record) {
        transactionHistory
                .computeIfAbsent(record.getCustomerName(), k -> new ArrayList<>())
                .add(record);
        
        log.debug("Saved transaction for customer: {}", record.getCustomerName());
    }
    
    /**
     * Get all transactions for a customer.
     */
    public List<TransactionRecord> getCustomerHistory(String customerName) {
        return new ArrayList<>(transactionHistory.getOrDefault(customerName, new ArrayList<>()));
    }
    
    /**
     * Get recent transactions within a time window.
     */
    public List<TransactionRecord> getRecentTransactions(String customerName, LocalDateTime since) {
        return getCustomerHistory(customerName).stream()
                .filter(record -> record.getTimestamp().isAfter(since))
                .collect(Collectors.toList());
    }
    
    /**
     * Check if customer has purchased from a specific location before.
     */
    public boolean hasVisitedLocation(String customerName, String city, String state) {
        return getCustomerHistory(customerName).stream()
                .anyMatch(record -> record.getCity().equalsIgnoreCase(city) 
                        && record.getState().equalsIgnoreCase(state));
    }
    
    /**
     * Count transactions in a time window (for velocity check).
     */
    public long countRecentTransactions(String customerName, LocalDateTime since) {
        return getRecentTransactions(customerName, since).size();
    }
}
