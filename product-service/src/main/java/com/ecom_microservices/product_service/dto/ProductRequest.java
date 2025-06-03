package com.ecom_microservices.product_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Product ID is mandatory")
    private String productId;

    @NotBlank(message = "Product name is mandatory")
    private String name;

    @Positive(message = "Price must be greater than zero")
    private double price;

    @NotBlank(message = "Category is mandatory")
    private String category;

    @NotNull(message = "Availability must be specified")
    private Boolean available;
}