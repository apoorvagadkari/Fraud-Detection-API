package com.example.demo.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a historical transaction record.
 * This is the internal data structure used to store a simplified version of each transaction in the history (ConcurrentHashMap), enabling fraud detection based on past customer behavior like location patterns and transaction velocity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRecord {
    
    private String customerName;
    private String city;
    private String state;
    private Double amount;
    private LocalDateTime timestamp;
    private String ipAddress;
    private String merchantName;
}
