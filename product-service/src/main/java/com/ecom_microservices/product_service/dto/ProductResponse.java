package com.ecom_microservices.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String productId;
    private String name;
    private double price;
    private String category;
    private int quantity;

    public ProductResponse(String p100, String laptop, double v, String electronics, boolean b) {
    }
}
