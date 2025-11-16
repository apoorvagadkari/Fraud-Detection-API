package com.example.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a geographic location with city and state.
 * Used in both customer location and merchant location.
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
