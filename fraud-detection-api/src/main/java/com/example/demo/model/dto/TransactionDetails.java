package com.example.demo.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Details about the merchant and transaction.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetails {
    
    @NotBlank(message = "Merchant name is required")
    private String merchantName;
    
    @NotNull(message = "Merchant location is required")
    @Valid
    private Location merchantLocation;
    
    @NotNull(message = "Item count is required")
    @Positive(message = "Item count must be greater than zero")
    private Integer purchasedItemCount;
}
