package com.ecom.payment.paymentservice.service;

import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
import com.ecom.payment.paymentservice.enums.PaymentStatus;
import com.ecom.payment.paymentservice.exception.InvalidOrderException;
import com.ecom.payment.paymentservice.exception.ResourceNotFoundException;
import com.ecom.payment.paymentservice.model.Payment;
import com.ecom.payment.paymentservice.repository.PaymentRepository;
import com.ecom.payment.paymentservice.utillity.PaymentGatewaySimulator;
import com.ecom.payment.paymentservice.utillity.PaymentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Service
//@Slf4j
//@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
//    private final PaymentRepository paymentRepository;
//    private final RestTemplate restTemplate;
//    private final PaymentMapper paymentMapper;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PaymentMapper paymentMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ORDER_SERVICE_BASE_URL = "http://ORDER-SERVICE/api/orders/";

    @Override
    @Transactional
    public PaymentResponseDTO initiatePayment(PaymentRequestDTO dto) {
        log.info("Initiating payment for orderId: {}", dto.getOrderId());

//        validateOrder(dto.getOrderId());
//        boolean hasSuccessfulPayment = paymentRepository.findByOrderId(dto.getOrderId()).stream()
//               .anyMatch(payment -> PaymentStatus.SUCCESS.equals(payment.getStatus()));

        boolean hasSuccessfulPayment=false;

        if (hasSuccessfulPayment) {
            log.warn("Payment already completed for orderId: {}", dto.getOrderId());
            throw new IllegalStateException("Payment already completed for order ID: " + dto.getOrderId());
        }

        Payment payment = paymentMapper.toEntity(dto);

        String result = PaymentGatewaySimulator.simulate(dto.getPaymentMethod());
        log.info("Payment gateway result for method {}: {}", dto.getPaymentMethod(), result);

        payment.setStatus(parsedPaymentStatus(result));
        PaymentResponseDTO response = paymentMapper.toDto(paymentRepository.save(payment));
        log.info("Final payment response after status update: {}", response);

        return paymentMapper.toDto(payment);
    }

    private void validateOrder(Long orderId) {
        try {
            restTemplate.getForObject(ORDER_SERVICE_BASE_URL + orderId, Object.class);
            log.info("Order ID {} is valid", orderId);
        }
        catch (Exception ex) {
            log.error("Invalid order ID: {}", orderId, ex);
            throw new InvalidOrderException("Invalid order ID: "+orderId);
        }
    }

    @Override
    @Transactional
    public PaymentResponseDTO updatePaymentStatus(Long paymentId, String status) {
        log.info("Updating payment status for ID: {} to {}", paymentId, status);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for ID: " + paymentId));

        PaymentStatus parsedStatus = parsedPaymentStatus(status);
        payment.setStatus(parsedStatus);

        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    private PaymentStatus parsedPaymentStatus(String status) {
        try {
            return PaymentStatus.valueOf(status.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid Payment Status: "+status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        log.info("Fetching payment by ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for ID: " + id));

        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByOrderId(Long orderId) {
        log.info("Fetching all payments for Order ID: {}", orderId);

        return paymentRepository.findByOrderId(orderId).stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentResponseDTO refundPayment(Long orderId) {
        log.info("Initiating refund for orderId: {}", orderId);
        validateOrder(orderId);
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        if (payments.isEmpty()) {
            throw new ResourceNotFoundException("No payments found for Order ID: " + orderId);
        }

        Payment successfulPayment = payments.stream()
                .filter(p -> PaymentStatus.SUCCESS.equals(p.getStatus()))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("No successful payment found to refund for Order ID: " + orderId)
                );


        successfulPayment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(successfulPayment);
        log.info("Payment ID {} marked as REFUNDED", successfulPayment.getPaymentId());

        updateOrderStatus(orderId, "REFUNDED");

        return paymentMapper.toDto(successfulPayment);
    }

    private void updateOrderStatus(Long orderId, String status) {
        try {
            restTemplate.put(ORDER_SERVICE_BASE_URL + orderId + "/status", status);
            log.info("Order status updated for orderId: {} with status: {}", orderId, status);
        } catch (Exception e) {
            log.error("Error updating order status for orderId: {}", orderId, e);
            throw new RuntimeException("Failed to update order status for orderId: " + orderId);
        }
    }
}

