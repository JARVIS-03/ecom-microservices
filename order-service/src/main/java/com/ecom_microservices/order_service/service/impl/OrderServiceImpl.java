package com.ecommerce.orderservice.service.impl;

import com.ecommerce.orderservice.dto.request.OrderRequest;
import com.ecommerce.orderservice.dto.response.OrderResponse;
import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.entity.OrderItem;
import com.ecommerce.orderservice.enums.OrderStatus;
import com.ecommerce.orderservice.exception.InvalidOrderStatusException;
import com.ecommerce.orderservice.exception.ResourceNotFoundException;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional
    @Retryable(
            value = { RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000))
    public OrderResponse createOrder(OrderRequest orderRequest) {
        log.info("Creating a new order for customer: {}", orderRequest.getCustomerIdentifier());
        Order order = modelMapper.map(orderRequest, Order.class);

        order.setTotalQuantity(calculateTotalQuantity(orderRequest.getOrderItems()));
        order.setTotalAmount(calculateTotalAmount(orderRequest.getOrderItems()));
        order.setOrderStatus(OrderStatus.PROCESSING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder=orderRepository.save(order);
        log.debug("Order saved: {}", savedOrder.getId());
        return modelMapper.map(savedOrder,OrderResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");
        List<OrderResponse> orderResponses=new ArrayList<>();

        for(Order order:orderRepository.findAll())
            orderResponses.add(modelMapper.map(order,OrderResponse.class));
        if(orderResponses.isEmpty()) {
            log.warn("No orders found");
            throw new NullPointerException("No Orders Found");
        }

        return orderResponses;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId) {
        log.info("Fetching order with ID: {}", orderId);
        Order order=orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Resource not found"));
        return modelMapper.map(order,OrderResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrderByCustomerId(UUID customerId) {
        log.info("Fetching orders for customer ID: {}", customerId);
        List<OrderResponse> orderResponses=new ArrayList<>();
        for(Order order:orderRepository.findByCustomerIdentifier(customerId))
            orderResponses.add(modelMapper.map(order,OrderResponse.class));
        return orderResponses;
    }

    @Override
    @Transactional
    @Retryable(
            value = { RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000))
    public OrderResponse updateOrder(UUID orderId, OrderRequest orderRequest) {
        log.info("Updating order with ID: {}", orderId);
        Order order=orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Resource not found"));

        order.setOrderItems(orderRequest.getOrderItems());
        order.setTotalQuantity(calculateTotalQuantity(orderRequest.getOrderItems()));
        order.setTotalAmount(calculateTotalAmount(orderRequest.getOrderItems()));
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);
        log.debug("Order updated: {}", updatedOrder.getId());
        return modelMapper.map(updatedOrder,OrderResponse.class);
    }

    @Override
    @Transactional
    @Retryable(
            value = { RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000))
    public void deleteOrder(UUID orderId) {
        log.info("Deleting order with ID: {}", orderId);
        if (!orderRepository.existsById(orderId)) {
            log.error("Order not found with ID: {}", orderId);
            throw new ResourceNotFoundException("Resource not found");
        }
        log.debug("Order deleted with ID: {}", orderId);
        orderRepository.deleteById(orderId);
    }

    @Override
    public OrderResponse cancelOrder(UUID orderId) {
        log.info("Cancelling order with ID: {}", orderId);
        Order order=orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Resource not found"));
        if(order.getOrderStatus().equals(OrderStatus.PROCESSING))
            order.setOrderStatus(OrderStatus.CANCELLED);
        else {
            log.warn("Cannot cancel order {} with status {}", orderId, order.getOrderStatus());
            throw new InvalidOrderStatusException("The order cannot be cancelled because it has already been shipped or completed or cancelled!");
        }

        Order cancelledOrder = orderRepository.save(order);
        log.debug("Order cancelled: {}", cancelledOrder.getId());
        return modelMapper.map(cancelledOrder,OrderResponse.class);
    }

    private long calculateTotalAmount(List<OrderItem> orderItems)
    {
        return orderItems.stream().mapToLong(item -> (long) item.getProductPrice() * item.getQuantity()).sum();
    }

    private int calculateTotalQuantity(List<OrderItem> orderItems)
    {
        return orderItems.stream().mapToInt(OrderItem::getQuantity).sum();
    }
}
