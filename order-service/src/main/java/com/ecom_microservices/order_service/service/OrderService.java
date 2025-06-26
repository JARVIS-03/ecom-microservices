package com.ecom_microservices.order_service.service;

import com.ecom_microservices.order_service.dto.request.OrderRequest;
import com.ecom_microservices.order_service.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);
    List<OrderResponse> getAllOrders();
    OrderResponse getOrder(long orderId);
    List<OrderResponse> getOrderByCustomerId(long customerId);
    OrderResponse updateOrder(long orderId,OrderRequest orderRequest);
    OrderResponse updateOrderStatus(Long orderId, String status);
    void deleteOrder(long orderId);
    OrderResponse cancelOrder(long orderId);
}
