package com.example.demo.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This holds information about the merchant and purchase (merchant name, merchant location, and number of items bought) with validation to ensure all required fields are provided.
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
