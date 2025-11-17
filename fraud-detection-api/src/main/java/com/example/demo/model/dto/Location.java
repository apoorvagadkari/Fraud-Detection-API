package com.example.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a geographic location with city and state.
 * This is a simple data structure that holds a city and state (like "Boston, MA"), used for both customer location and merchant location in the transaction request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "State is required")
    private String state;
}
