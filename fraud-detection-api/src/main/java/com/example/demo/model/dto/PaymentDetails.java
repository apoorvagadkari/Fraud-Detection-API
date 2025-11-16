package com.example.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payment details for the transaction.
 * Contains card information and transaction amount.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetails {
    
    @NotBlank(message = "Card last 4 digits are required")
    private String cardLast4;
    
    @NotBlank(message = "Name on card is required")
    private String nameOnCard;
    
    @NotNull(message = "Transaction amount is required")
    @Positive(message = "Transaction amount must be greater than zero")
    private Double cardAmount;
}
