package com.example.demo.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a historical transaction record.
 * Used for detecting fraud patterns based on customer history.
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
