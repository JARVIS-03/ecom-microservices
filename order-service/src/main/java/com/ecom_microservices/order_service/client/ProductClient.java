package com.ecom_microservices.order_service.client;

import com.ecom_microservices.order_service.dto.response.ProductResponse;
import com.ecom_microservices.order_service.entity.OrderItem;
import com.ecom_microservices.order_service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductClient {

    private final RestTemplate restTemplate;

    public final String PRODUCT_SERVICE_URL="http://PRODUCT-SERVICE/api/products/";

    public void validateProduct(String productId)
    {
        String productServiceUrl = PRODUCT_SERVICE_URL + productId;

        ProductResponse productResponse=restTemplate.getForObject(productServiceUrl, ProductResponse.class);
        if (productResponse.getName().equals("Unavailable"))
            throw new ResourceNotFoundException("Product not found");
        else
            log.debug("Product ID {} is valid", productId);
    }

    public long calculateTotalAmount(List<OrderItem> orderItems)
    {
        if(orderItems.isEmpty())
            throw new NullPointerException("Order items are Empty");
        return orderItems.stream().mapToLong(item -> (long) item.getProductPrice() * item.getQuantity()).sum();
    }

    public int calculateTotalQuantity(List<OrderItem> orderItems)
    {
        if(orderItems.isEmpty())
            throw new NullPointerException("Order items are is Empty");
        return orderItems.stream().mapToInt(OrderItem::getQuantity).sum();
    }
}
