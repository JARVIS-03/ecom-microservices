package com.ecom_microservices.order_service.service.impl;

import com.ecom_microservices.order_service.dto.request.NotificationRequest;
import com.ecom_microservices.order_service.dto.request.OrderRequest;
import com.ecom_microservices.order_service.dto.request.PaymentRequest;
import com.ecom_microservices.order_service.dto.response.OrderResponse;
import com.ecom_microservices.order_service.dto.response.ProductResponse;
import com.ecom_microservices.order_service.entity.Order;
import com.ecom_microservices.order_service.entity.OrderItem;
import com.ecom_microservices.order_service.enums.OrderStatus;
import com.ecom_microservices.order_service.exception.InvalidOrderStatusException;
import com.ecom_microservices.order_service.exception.ResourceNotFoundException;
import com.ecom_microservices.order_service.repository.OrderRepository;
import com.ecom_microservices.order_service.service.OrderKafkaService;
import com.ecom_microservices.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final ModelMapper modelMapper;

    private final RestTemplate restTemplate;

    private final OrderKafkaService kafkaService;

    @Override
    @Transactional
    @Retryable(
            value = { RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000))
    public OrderResponse createOrder(OrderRequest orderRequest) {
        log.info("Creating a new order for customer: {}", orderRequest.getCustomerIdentifier());

        for(OrderItem orderItems:orderRequest.getOrderItems())
            validateProduct(orderItems.getProductId());

        Order order = modelMapper.map(orderRequest, Order.class);
        order.setTotalQuantity(calculateTotalQuantity(orderRequest.getOrderItems()));
        order.setTotalAmount(calculateTotalAmount(orderRequest.getOrderItems()));
        order.setOrderStatus(OrderStatus.PROCESSING);

        Order savedOrder=orderRepository.save(order);
        sendNotification(savedOrder);

        Order saveOrder=orderRepository.save(order);
        sendPaymentNotification(saveOrder);

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

    @Override
    @Transactional
    @Retryable(
            value = { RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000))
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        order.setOrderStatus(OrderStatus.PROCESSING);
//        order.setOrderStatus(status);
        return modelMapper.map(orderRepository.save(order),OrderResponse.class);
    }

    private void validateProduct(String productId)
    {
        String productServiceUrl = "http://PRODUCT-SERVICE/api/products/" + productId;

            ProductResponse productResponse=restTemplate.getForObject(productServiceUrl, ProductResponse.class);
            if (productResponse.getName().equals("Unavailable"))
                throw new ResourceNotFoundException("Product not found");
            else
                log.debug("Product ID {} is valid", productId);
    }

    private void sendNotification(Order savedOrder) {
        NotificationRequest notificationRequest=NotificationRequest.builder()
                .orderId(savedOrder.getId())
                .userEmail("2002mohan@gmail.com")
                .status(savedOrder.getOrderStatus())
                .build();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<NotificationRequest> request = new HttpEntity<>(notificationRequest, headers);
            kafkaService.sendOrderNotification(notificationRequest);
            restTemplate.postForLocation("http://NOTIFICATION-SERVICE/api/notifications/order/send", request);
            log.info("Notification sent successfully.");
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
        }
    }

    private void sendPaymentNotification(Order savedOrder) {
        PaymentRequest paymentRequest=PaymentRequest.builder()
                .orderId(savedOrder.getId())
                .paymentMethod("CREDIT_CARD")
                .amount(savedOrder.getTotalAmount())

                .build();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<PaymentRequest> request = new HttpEntity<>(paymentRequest, headers);

            restTemplate.postForLocation("http://PAYMENTSERVICE/api/payments/initiate", request, savedOrder.getId());
            log.info("Payment sent successfully.");
        } catch (Exception e) {
            log.error("Failed to send Payment: {}", e.getMessage());
        }
    }



    private long calculateTotalAmount(List<OrderItem> orderItems)
    {
        if(orderItems.isEmpty())
            throw new NullPointerException("Order items are Empty");
        return orderItems.stream().mapToLong(item -> (long) item.getProductPrice() * item.getQuantity()).sum();
    }

    private int calculateTotalQuantity(List<OrderItem> orderItems)
    {
        if(orderItems.isEmpty())
            throw new NullPointerException("Order items are is Empty");
        return orderItems.stream().mapToInt(OrderItem::getQuantity).sum();
    }
}
