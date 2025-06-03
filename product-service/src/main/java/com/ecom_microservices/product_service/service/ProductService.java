package com.ecom_microservices.product_service.service;


import com.ecom_microservices.product_service.dto.ProductResponse;
import com.ecom_microservices.product_service.exception.ProductNotFoundException;
import com.ecom_microservices.product_service.model.Product;
import com.ecom_microservices.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ProductService {


    @Autowired
    private ProductRepository productRepository;

    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000),
            exclude = ProductNotFoundException.class
    )
    @Transactional(readOnly = true)
    public ProductResponse getProductById(String productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found with ID: " + productId
                ));

        return mapToProductResponse(product);
    }

    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }


    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .category(product.getCategory())
                .available(product.isAvailable())
                .build();
    }
}
