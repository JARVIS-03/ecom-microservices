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
    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        if (!ALLOWED_CATEGORIES.contains(category)) {
            throw new InvalidCategoryException(category);
        }
        return productRepository.findByCategory(category);
    }
      @Transactional
    public ProductResponse updateProduct(String productId, ProductRequest productRequest) {
        validateProductRequest(productRequest);

        Product existingProduct = productRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found with ID: " + productId
                ));

        updateProductDetails(existingProduct, productRequest);
        Product updatedProduct = productRepository.save(existingProduct);

        log.info("Product updated successfully with ID: {}", productId);
        return mapToProductResponse(updatedProduct);
    }
     @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        validateProductRequest(productRequest);

        if (productRepository.existsByProductId(productRequest.getProductId())) {
            throw new ValidationException(
                    "Product with ID " + productRequest.getProductId() + " already exists"
            );
        }

        Product product = mapToProduct(productRequest);
        product = productRepository.save(product);

        log.info("Product created successfully with ID: {}", product.getProductId());
        return mapToProductResponse(product);
    }
    
}
