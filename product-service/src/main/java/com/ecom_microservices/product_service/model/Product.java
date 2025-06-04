package com.ecom_microservices.product_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

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
    @Length(min = 5, max = 10, message = "Product ID must be between 5 and 10 characters")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Product ID must be alphanumeric")
    private String productId;

    @NotBlank(message = "Product name is mandatory")
    private String name;

    @Positive(message = "Price must be greater than zero")
    private double price;

    @NotBlank(message = "Category is mandatory")
    private String category;

    @Min(value = 0, message = "Quantity must be zero or greater")
    private int quantity;
}
