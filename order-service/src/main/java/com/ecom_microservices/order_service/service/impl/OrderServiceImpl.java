package com.ecom_microservices.order_service.service.impl;

import com.ecom_microservices.order_service.client.NotificationClient;
import com.ecom_microservices.order_service.client.PaymentClient;
import com.ecom_microservices.order_service.client.ProductClient;
import com.ecom_microservices.order_service.dto.request.OrderRequest;
import com.ecom_microservices.order_service.dto.response.OrderResponse;
import com.ecom_microservices.order_service.entity.Order;
import com.ecom_microservices.order_service.entity.OrderItem;
import com.ecom_microservices.order_service.enums.OrderStatus;
import com.ecom_microservices.order_service.exception.InvalidOrderStatusException;
import com.ecom_microservices.order_service.exception.ResourceNotFoundException;
import com.ecom_microservices.order_service.repository.OrderRepository;
import com.ecom_microservices.order_service.service.OrderService;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final ModelMapper modelMapper;

    private final ProductClient productClient;

    private final NotificationClient notificationClient;

    private final PaymentClient paymentClient;

    @Override
    @Transactional
    @Retryable(
            value = { RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000))
    public OrderResponse createOrder(OrderRequest orderRequest) {
        log.info("Creating a new order for customer: {}", orderRequest.getCustomerIdentifier());

        for(OrderItem orderItems:orderRequest.getOrderItems())
            productClient.validateProduct(orderItems.getProductId());

        Order order = modelMapper.map(orderRequest, Order.class);
        order.setTotalQuantity(productClient.calculateTotalQuantity(orderRequest.getOrderItems()));
        order.setTotalAmount(productClient.calculateTotalAmount(orderRequest.getOrderItems()));
        order.setOrderStatus(OrderStatus.NEW);

        Order savedOrder=orderRepository.save(order);
        notificationClient.sendNotification(savedOrder);
        paymentClient.sendPaymentNotification(savedOrder);

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
        return orderResponses;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(long orderId) {
        log.info("Fetching order with ID: {}", orderId);
        Order order=orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Resource not found"));
        return modelMapper.map(order,OrderResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrderByCustomerId(long customerId) {
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
    public OrderResponse updateOrder(long orderId, OrderRequest orderRequest) {
        log.info("Updating order with ID: {}", orderId);
        Order order=orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Resource not found"));

        order.setOrderItems(orderRequest.getOrderItems());
        order.setTotalQuantity(productClient.calculateTotalQuantity(orderRequest.getOrderItems()));
        order.setTotalAmount(productClient.calculateTotalAmount(orderRequest.getOrderItems()));
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
    public void deleteOrder(long orderId) {
        log.info("Deleting order with ID: {}", orderId);
        if (!orderRepository.existsById(orderId)) {
            log.error("Order not found with ID: {}", orderId);
            throw new ResourceNotFoundException("Resource not found");
        }
        log.debug("Order deleted with ID: {}", orderId);
        orderRepository.deleteById(orderId);
    }

    @Override
    @Transactional
    @Retryable(
            value = { RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000))
    public OrderResponse cancelOrder(long orderId) {
        log.info("Cancelling order with ID: {}", orderId);
        Order order=orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Resource not found"));
        if(order.getOrderStatus().equals(OrderStatus.NEW) || order.getOrderStatus().equals(OrderStatus.PROCESSING))
            order.setOrderStatus(OrderStatus.CANCELLED);
        else {
            log.warn("Cannot cancel order {} with status {}", orderId, order.getOrderStatus());
            throw new InvalidOrderStatusException("The order cannot be cancelled because it has already been shipped or completed or cancelled!");
        }

        Order cancelledOrder = orderRepository.save(order);
        log.debug("Order cancelled: {}", cancelledOrder.getId());
        return modelMapper.map(cancelledOrder,OrderResponse.class);
    }

    @Override
    @Transactional
    @Retryable(
            value = { RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000))
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        if (status.equals("SUCCESS")) {
            order.setOrderStatus(OrderStatus.PROCESSING);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            System.out.println(status);

//             Schedule update to DELIVERED after 5 seconds
            ScheduledExecutorService shippingScheduler = Executors.newSingleThreadScheduledExecutor();
            shippingScheduler.schedule(() -> {
                order.setOrderStatus(OrderStatus.SHIPPED);
                order.setUpdatedAt(LocalDateTime.now());
                order.setShippedAt(LocalDateTime.now());
                orderRepository.save(order);
            }, 1, TimeUnit.DAYS);

            ScheduledExecutorService deliveryScheduler = Executors.newSingleThreadScheduledExecutor();
            deliveryScheduler.schedule(() -> {
                order.setOrderStatus(OrderStatus.DELIVERED);
                order.setUpdatedAt(LocalDateTime.now());
                order.setDeliveredAt(LocalDateTime.now());
                orderRepository.save(order);
            }, 3, TimeUnit.DAYS);

        }
        else if (status.equals("FAILED") || status.equalsIgnoreCase("REFUNDED")) {
            order.setOrderStatus(OrderStatus.CANCELLED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        }
        return modelMapper.map(order,OrderResponse.class);
    }
}
