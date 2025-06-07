package com.ecom_microservices.product_service.product_controller_test;

import com.ecom_microservices.product_service.controller.ProductController;
import com.ecom_microservices.product_service.dto.ProductRequest;
import com.ecom_microservices.product_service.dto.ProductResponse;
import com.ecom_microservices.product_service.exception.ProductNotFoundException;
import com.ecom_microservices.product_service.model.Product;
import com.ecom_microservices.product_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    // Success case for all APIs
    @Test
    void allAPIs_Success() {
        ProductRequest request = new ProductRequest("P100", "Laptop", 999.99, "Electronics", 10);
        ProductResponse response = new ProductResponse("P100", "Laptop", 999.99, "Electronics", 10);
        List<ProductResponse> productList = List.of(response);

        when(productService.createProduct(request)).thenReturn(response);
        when(productService.getProductById("P100")).thenReturn(response);
        when(productService.getAllProducts()).thenReturn(List.of(new Product()));
        when(productService.updateProduct("P100", request)).thenReturn(response);

        assertEquals(HttpStatus.CREATED, productController.createProduct(request).getStatusCode());
        assertEquals(HttpStatus.OK, productController.getProductById("P100").getStatusCode());
        assertEquals(HttpStatus.OK, productController.getAllProducts().getStatusCode());
        assertEquals(HttpStatus.OK, productController.updateProduct("P100", request).getStatusCode());
    }

    // Failure case for all APIs
    @Test
    void allAPIs_Failure() {
        when(productService.getProductById("INVALID")).thenThrow(new ProductNotFoundException("Not found"));
        when(productService.getAllProducts()).thenThrow(new RuntimeException("Database error"));

        assertThrows(ProductNotFoundException.class, () -> productController.getProductById("INVALID"));
        assertThrows(RuntimeException.class, () -> productController.getAllProducts());
    }
}