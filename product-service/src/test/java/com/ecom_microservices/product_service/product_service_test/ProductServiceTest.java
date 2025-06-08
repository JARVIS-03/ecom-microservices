package com.ecom_microservices.product_service.product_service_test;

import com.ecom_microservices.product_service.dto.ProductRequest;
import com.ecom_microservices.product_service.dto.ProductResponse;
import com.ecom_microservices.product_service.exception.ProductNotFoundException;
import com.ecom_microservices.product_service.model.Product;
import com.ecom_microservices.product_service.repository.ProductRepository;
import com.ecom_microservices.product_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    // Success case for all methods
    @Test
    void allMethods_Success() {
        ProductRequest request = new ProductRequest("P100", "Laptop", 999.99, "Electronics", 10);
        Product product = new Product(1L, "P100", "Laptop", 999.99, "Electronics", 10);
        ProductResponse response = new ProductResponse("P100", "Laptop", 999.99, "Electronics", 10);

        when(productRepository.save(any())).thenReturn(product);
        when(productRepository.findByProductId("P100")).thenReturn(Optional.of(product));
        when(productRepository.findAll()).thenReturn(List.of(product));

        assertNotNull(productService.createProduct(request));
        assertEquals("P100", productService.getProductById("P100").getProductId());
        assertFalse(productService.getAllProducts().isEmpty());
        assertNotNull(productService.updateProduct("P100", request));
    }

    // Failure case for all methods
    @Test
    void allMethods_Failure() {
        when(productRepository.findByProductId("INVALID")).thenReturn(Optional.empty());
        when(productRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById("INVALID"));
        assertThrows(RuntimeException.class, () -> productService.getAllProducts());
    }
}