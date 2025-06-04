package com.ecom_microservices.product_service.service;


import com.ecom_microservices.product_service.dto.ProductRequest;
import com.ecom_microservices.product_service.dto.ProductResponse;
import com.ecom_microservices.product_service.exception.InvalidCategoryException;
import com.ecom_microservices.product_service.exception.ProductNotFoundException;
import com.ecom_microservices.product_service.exception.ValidationException;
import com.ecom_microservices.product_service.model.Product;
import com.ecom_microservices.product_service.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private static final List<String> ALLOWED_CATEGORIES = Arrays.asList(
            "Electronics", "Books", "Clothing", "Home"
    );

    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000),
            exclude = ProductNotFoundException.class
    )

    @Transactional(readOnly = true)
    public ProductResponse getProductById(String productId) {
        log.debug("Attempting to fetch product with ID: {}", productId);

        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", productId);
                    return new ProductNotFoundException("Product not found with ID: " + productId);
                });

        log.info("Product found with ID: {}", productId);
        return mapToProductResponse(product);
    }

    @Retryable(
            value = { RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public List<Product> getAllProducts() {
        log.debug("Fetching all products from the repository...");

        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            log.warn("Product list is empty, triggering retry...");
            throw new RuntimeException("Product list is empty, retrying...");
        }

        log.info("Successfully fetched {} products.", products.size());
        return products;
    }

    @Recover
    public List<Product> recover(RuntimeException e) {
        log.error("Retries exhausted for getAllProducts(): {}", e.getMessage());
        return List.of();
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

    @Transactional
    public void deleteProduct(String productId) {
        if (!productRepository.existsByProductId(productId)) {
            throw new ProductNotFoundException(
                    "Product not found with ID: " + productId
            );
        }

        productRepository.deleteByProductId(productId);
        log.info("Product deleted successfully with ID: {}", productId);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) {
        log.debug("Fetching products with price between {} and {}", minPrice, maxPrice);
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Transactional(readOnly = true)
    public Page<Product> getPaginatedProducts(int page, int size) {
        log.debug("Fetching paginated products with page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    private void validateProductRequest(ProductRequest request) {
        if (request.getQuantity() < 0) {
            throw new ValidationException("Quantity must be zero or greater");
        }

        if (!ALLOWED_CATEGORIES.contains(request.getCategory())) {
            throw new InvalidCategoryException(request.getCategory());
        }
    }
    private Product mapToProduct(ProductRequest request) {
        return Product.builder()
                .productId(request.getProductId())
                .name(request.getName())
                .price(request.getPrice())
                .category(request.getCategory())
                .quantity(request.getQuantity())
                .build();
    }

    private void updateProductDetails(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setQuantity(request.getQuantity());
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .category(product.getCategory())
                .quantity(product.getQuantity())
                .build();
    }

    
}
