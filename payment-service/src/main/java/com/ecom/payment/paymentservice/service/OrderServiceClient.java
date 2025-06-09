package com.ecom.payment.paymentservice.service;


import com.ecom.payment.paymentservice.exception.PaymentException;
import com.ecom.payment.paymentservice.model.ErrorCode;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
//@RequiredArgsConstructor
//@Slf4j
public class OrderServiceClient {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceClient.class);
    @Autowired
    private RestTemplate restTemplate;
    private static final String ORDER_SERVICE_BASE_URL = "http://ORDER-SERVICE/api/orders/";

    @CircuitBreaker(name = "orderService", fallbackMethod = "validateOrderFallback")
    @Retry(name = "orderService")
    public void validateOrder(Long orderId) {
        Object response = restTemplate.getForObject(ORDER_SERVICE_BASE_URL + orderId, Object.class);
        if (response == null) {
            throw new PaymentException(ErrorCode.PAYMENT_VALIDATION_FAILED);

        }
        log.info("Order ID {} is valid", orderId);
    }

    public void validateOrderFallback(Long orderId, Throwable t) {
        log.error("Order service fallback triggered for orderId: {}", orderId, t);
        throw new PaymentException(ErrorCode.PAYMENT_VALIDATION_FAILED);

    }

    @CircuitBreaker(name = "orderService", fallbackMethod = "updateOrderStatusFallback")
    @Retry(name = "orderService")
    public void updateOrderStatus(Long orderId, String status) {
        restTemplate.put(ORDER_SERVICE_BASE_URL + orderId + "/status", status);
        log.info("Order status updated to '{}' for orderId: {}", status, orderId);
    }

    public void updateOrderStatusFallback(Long orderId, String status, Throwable t) {
        log.error("Fallback triggered while updating order status for orderId: {}, status: {}", orderId, status, t);
        throw new PaymentException(ErrorCode.PAYMENT_INTERNAL_ERROR);
    }
}