package com.example.demo.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Main request DTO for the fraud scoring API.
 * Represents the complete transaction information sent by the merchant.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @NotBlank(message = "IP address is required")
    private String ipAddress;
    
    @NotNull(message = "Location is required")
    @Valid
    private Location location;
    
    @NotNull(message = "Payment details are required")
    @Valid
    private PaymentDetails paymentDetails;
    
    @NotNull(message = "Transaction details are required")
    @Valid
    private TransactionDetails transactionDetails;
}
