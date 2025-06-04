package com.ecom_microservices.product_service.controller;

import com.ecom_microservices.product_service.dto.ProductRequest;
import com.ecom_microservices.product_service.dto.ProductResponse;
import com.ecom_microservices.product_service.model.Product;
import com.ecom_microservices.product_service.service.ProductService;
import com.ecom_microservices.product_service.exception.ValidationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        log.info("Received request to get all products");
        List<Product> products = productService.getAllProducts();
        log.info("Returning {} products", products.size());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String productId) {
        log.info("Received request to get product by ID: {}", productId);
        ProductResponse response = productService.getProductById(productId);
        log.info("Returning product with ID: {}", productId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addProduct")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        log.info("Received request to create new product: {}", productRequest);
        ProductResponse response = productService.createProduct(productRequest);
        log.info("Created new product with ID: {}", response.getProductId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String productId,
            @Valid @RequestBody ProductRequest productRequest) {
        log.info("Received request to update product with ID: {}, data: {}", productId, productRequest);
        ProductResponse response = productService.updateProduct(productId, productRequest);
        log.info("Updated product with ID: {}", productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        log.info("Received request to get products by category: {}", category);
        List<Product> products = productService.getProductsByCategory(category);
        log.info("Returning {} products for category: {}", products.size(), category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<Product>> searchProductsByName(@PathVariable String keyword) {
        log.info("Received request to search products with keyword: {}", keyword);
        List<Product> products = productService.searchProductsByName(keyword);
        log.info("Found {} products matching keyword: {}", products.size(), keyword);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        log.info("Received request to delete product with ID: {}", productId);
        productService.deleteProduct(productId);
        log.info("Deleted product with ID: {}", productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<ProductResponse>> getProductsByPriceRange(
            @RequestParam @Min(value = 0, message = "minPrice must be zero or greater") double minPrice, 
            @RequestParam @Min(value = 0, message = "maxPrice must be zero or greater") double maxPrice) {
        log.info("Received request to get products by price range: {} - {}", minPrice, maxPrice);

        if (minPrice > maxPrice) {
            log.error("minPrice cannot be greater than maxPrice");
            throw new ValidationException("minPrice cannot be greater than maxPrice");
        }
        
        List<ProductResponse> productList = productService.getProductsByPriceRange(minPrice, maxPrice);
        
        log.info("Returning {} products within the price range", productList.size());
        return ResponseEntity.ok(productList);
    }

    @GetMapping("/paginated")
    public ResponseEntity<List<ProductResponse>> getPaginatedProducts(
            @RequestParam @Min(value = 0, message = "Page number must be zero or greater") int page, 
            @RequestParam @Positive(message = "Page size must be greater than zero") int size) {
        log.info("Received request to get paginated products, page: {}, size: {}", page, size);
        
        List<ProductResponse> productList = productService.getPaginatedProducts(page, size);
        
        log.info("Returning page {} of products with size {}", page, productList.size());
        return ResponseEntity.ok(productList);
    }
}