package com.ecom_microservices.product_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidCategoryException extends ValidationException {
    public InvalidCategoryException(String category) {
        super("Invalid product category: " + category +
                ". Allowed categories are: Electronics, Books, Clothing, Home");
    }
}