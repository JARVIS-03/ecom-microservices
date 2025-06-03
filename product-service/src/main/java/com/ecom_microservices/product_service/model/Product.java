package com.ecom_microservices.product_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product ID is mandatory")
    @Column(unique = true, nullable = false)
    private String productId;

    @NotBlank(message = "Product name is mandatory")
    private String name;

    @Positive(message = "Price must be greater than zero")
    private double price;

    @NotBlank(message = "Category is mandatory")
    private String category;

    private boolean available;
}
