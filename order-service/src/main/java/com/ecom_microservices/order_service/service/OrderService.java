package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.request.OrderRequest;
import com.ecommerce.orderservice.dto.response.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);
    List<OrderResponse> getAllOrders();
    OrderResponse getOrder(UUID orderId);
    List<OrderResponse> getOrderByCustomerId(UUID customerId);
    OrderResponse updateOrder(UUID orderId,OrderRequest orderRequest);
    void deleteOrder(UUID orderId);
    OrderResponse cancelOrder(UUID orderId);
}
