package com.ecom.payment.paymentservice.service;

import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
import com.ecom.payment.paymentservice.enums.PaymentStatus;
import com.ecom.payment.paymentservice.exception.PaymentException;
import com.ecom.payment.paymentservice.model.Payment;
import com.ecom.payment.paymentservice.model.ErrorCode;
import com.ecom.payment.paymentservice.repository.PaymentRepository;
import com.ecom.payment.paymentservice.retry.RetryLogger;
import com.ecom.payment.paymentservice.utillity.PaymentConverter;
import com.ecom.payment.paymentservice.utillity.PaymentGatewaySimulator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
//@RequiredArgsConstructor
//@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentConverter paymentConverter;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderServiceClient orderServiceClient;

    @Autowired
    private NotificationServiceClient notificationServiceClient;

    @Autowired
    private final RetryLogger retryLogger;

    @Autowired
    private final PaymentGatewaySimulator paymentGatewaySimulator;

    public PaymentServiceImpl(RetryLogger retryLogger, PaymentGatewaySimulator paymentGatewaySimulator) {
        this.retryLogger = retryLogger;
        this.paymentGatewaySimulator = paymentGatewaySimulator;
    }

    @Override
    @Transactional
    public PaymentResponseDTO initiatePayment(PaymentRequestDTO dto) {
        log.info("Initiating payment for orderId: {}", dto.getOrderId());

        // Validate order via separate OrderServiceClient
//        orderServiceClient.validateOrder(dto.getOrderId());
//
//        // Check if payment already successful
//        boolean hasSuccessfulPayment = paymentRepository.findByOrderId(dto.getOrderId()).stream()
//                .anyMatch(payment -> PaymentStatus.SUCCESS.equals(payment.getStatus()));
        boolean hasSuccessfulPayment=false;

        if (hasSuccessfulPayment) {
            log.warn("Payment already completed for orderId: {}", dto.getOrderId());
//            throw new IllegalStateException("Payment already completed for order ID: " + dto.getOrderId());
            throw new PaymentException(ErrorCode.PAYMENT_INTERNAL_ERROR);
        }

        Payment payment = buildPaymentEntity(dto);
        payment = savePaymentWithRetry(payment);
        log.info("Payment saved with ID: {}", payment.getPaymentId());

        String result = paymentGatewaySimulator.simulate(dto.getPaymentMethod());
        log.info("Payment gateway result for method {}: {}", dto.getPaymentMethod(), result);

        PaymentResponseDTO response = updatePaymentStatus(payment.getPaymentId(), result);
        log.info("Final payment response after status update: {}", response);

        notificationServiceClient.sendNotification(response, ""); // Provide email as needed

        return response;
    }

    @Retryable(
            retryFor = DataAccessException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000),
            listeners = "retryLogger"
    )
    public Payment savePaymentWithRetry(Payment payment) {
        log.info("Attempting to save payment with orderID: {}", payment.getOrderId());
        return paymentRepository.save(payment);
    }

    @Recover
    public Payment recoverSavePayment(DataAccessException ex, Payment payment) {
        log.error("Failed to save payment after retries for orderID: {}", payment.getOrderId(), ex);
//        throw new IllegalStateException("Could not save payment after retries");
        throw new PaymentException(ErrorCode.PAYMENT_INTERNAL_ERROR);

    }

    private Payment buildPaymentEntity(PaymentRequestDTO dto) {
        Payment payment = new Payment();
        payment.setOrderId(dto.getOrderId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(PaymentStatus.INITIATED);
        payment.setDate(LocalDateTime.now());

        try {
            String methodDetails = objectMapper.writeValueAsString(dto.getMethodDetails());
            payment.setPaymentDetails(methodDetails);
            log.debug("Serialized method details: {}", methodDetails);
        } catch (JsonProcessingException e) {
            log.error("Error serializing method details", e);
            throw new PaymentException(ErrorCode.PAYMENT_VALIDATION_FAILED);
        }

        return payment;
    }

    @Override
    @Transactional
    public PaymentResponseDTO updatePaymentStatus(Long paymentId, String status) {
        log.info("Updating payment status for ID: {} to {}", paymentId, status);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

        PaymentStatus parsedStatus = parsedPaymentStatus(status);
        payment.setStatus(parsedStatus);

        return paymentConverter.toDTO(paymentRepository.save(payment));
    }

    private PaymentStatus parsedPaymentStatus(String status) {
        try {
            return PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
//            throw new IllegalStateException("Invalid Payment Status: " + status);
            throw new PaymentException(ErrorCode.PAYMENT_INTERNAL_ERROR);

        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        log.info("Fetching payment by ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

        return paymentConverter.toDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByOrderId(Long orderId) {
        log.info("Fetching all payments for Order ID: {}", orderId);

        return paymentRepository.findByOrderId(orderId).stream()
                .map(paymentConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentResponseDTO refundPayment(Long orderId) {
        log.info("Initiating refund for orderId: {}", orderId);

        orderServiceClient.validateOrder(orderId);

        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        if (payments.isEmpty()) {
//            throw new ResourceNotFoundException("No payments found for Order ID: " + orderId);
            throw new PaymentException(ErrorCode.PAYMENT_INTERNAL_ERROR);

        }

        Payment successfulPayment = payments.stream()
                .filter(p -> PaymentStatus.SUCCESS.equals(p.getStatus()))
                .findFirst()
                .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

        successfulPayment.setStatus(PaymentStatus.REFUNDED);
        PaymentResponseDTO response = paymentConverter.toDTO(paymentRepository.save(successfulPayment));
        log.info("Payment ID {} marked as REFUNDED", successfulPayment.getPaymentId());

        orderServiceClient.updateOrderStatus(orderId, "REFUNDED");

        notificationServiceClient.sendNotification(response, "vamsikrishnamavilla@gmail.com");

        return response;
    }


}

