package com.ecom.payment.paymentservice.service.impl;

import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
import com.ecom.payment.paymentservice.enums.PaymentStatus;
import com.ecom.payment.paymentservice.exception.PaymentException;
import com.ecom.payment.paymentservice.exception.model.ErrorCode;
import com.ecom.payment.paymentservice.model.Payment;
import com.ecom.payment.paymentservice.repository.PaymentRepository;
import com.ecom.payment.paymentservice.retry.RetryLogger;
import com.ecom.payment.paymentservice.service.NotificationServiceClient;
import com.ecom.payment.paymentservice.service.OrderServiceClient;
import com.ecom.payment.paymentservice.service.PaymentService;
import com.ecom.payment.paymentservice.utillity.PaymentGatewaySimulator;
import com.ecom.payment.paymentservice.utillity.PaymentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private OrderServiceClient orderServiceClient;
    
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private NotificationServiceClient notificationServiceClient;

    @Autowired
    private RetryLogger retryLogger;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private PaymentGatewaySimulator paymentGatewaySimulator;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public PaymentResponseDTO initiatePayment(PaymentRequestDTO dto) {
        log.info("Initiating payment for orderId: {}", dto.getOrderId());

//        Payment payment = PaymentRequestDTOtoPaymentMapper.INSTANCE.map(dto);
//        payment.setStatus(PaymentStatus.INITIATED);
//
//        try {
//            String methodDetails = objectMapper.writeValueAsString(dto.getMethodDetails());
//            payment.setPaymentDetails(methodDetails);
//        } catch (Exception e) {
//            log.error("Error serializing method details", e);
//            throw new PaymentException(ErrorCode.PAYMENT_INTERNAL_ERROR);
//        }

//        payment = paymentRepository.save(payment);
//        String result = mockPaymentGateway(dto.getPaymentMethod());
//        log.info("Payment gateway result: {}", result);
//        return updatePaymentStatus(payment.getPaymentId(), result);
//    }
        boolean hasSuccessfulPayment = paymentRepository.findByOrderId(dto.getOrderId()).stream()
                .anyMatch(payment -> PaymentStatus.SUCCESS.equals(payment.getStatus()));


        if (hasSuccessfulPayment) {
            log.warn("Payment already completed for orderId: {}", dto.getOrderId());
            throw new PaymentException(ErrorCode.PAYMENT_ALREADY_PROCESSED);
        }

        Payment payment = paymentMapper.toEntity(dto);
        payment = savePaymentWithRetry(payment);
        log.info("Payment saved with ID: {}", payment.getPaymentId());


        String result = paymentGatewaySimulator.simulate(dto.getPaymentMethod());
        log.info("Payment gateway result for method {}: {}", dto.getPaymentMethod(), result);

        PaymentResponseDTO response = updatePaymentStatus(payment.getPaymentId(), result);
        log.info("Final payment response after status update: {}", response);

//        notificationServiceClient.sendNotification(response); // Provide email as needed

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
        throw new PaymentException(ErrorCode.PAYMENT_RETRY_FAILED);
    }
    
    private String mockPaymentGateway(String method) {
        return Math.random() > 0.5 ? "SUCCESS" : "FAILED";
    }

    public void updateOrderStatus(String orderId, String status) {
        String url = "http://ORDER-SERVICE/api/orders/" + orderId + "/status";
        restTemplate.put(url, status);
    }

    @Override
    @Transactional
    public PaymentResponseDTO updatePaymentStatus(Long paymentId, String status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

        PaymentStatus paymentStatus = mapStatus(status);
        payment.setStatus(paymentStatus);

        payment = paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    private PaymentStatus parsedPaymentStatus(String status) {
        try {
            return PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new PaymentException(ErrorCode.PAYMENT_INVALID_STATUS);
        }
    }

    private PaymentStatus mapStatus(String status) {
        if (status == null) {
            throw new PaymentException(ErrorCode.PAYMENT_INVALID_STATUS);
        }

        try {
            return PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new PaymentException(ErrorCode.PAYMENT_INVALID_STATUS);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        log.info("Fetching payment by ID: {}", id);
        return paymentMapper.toDto(paymentRepository.findById(id).orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND)));
    }



    @Override
    @Transactional
    public PaymentResponseDTO refundPayment(Long orderId) {
        log.info("Initiating refund for orderId: {}", orderId);

        orderServiceClient.validateOrder(orderId);

        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        if (payments.isEmpty()) {
            throw new PaymentException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        Payment successfulPayment = payments.stream()
                .filter(p -> PaymentStatus.SUCCESS.equals(p.getStatus()))
                .findFirst()
                .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

        successfulPayment.setStatus(PaymentStatus.REFUNDED);
        PaymentResponseDTO response = paymentMapper.toDto(paymentRepository.save(successfulPayment));
        log.info("Payment ID {} marked as REFUNDED", successfulPayment.getPaymentId());

        orderServiceClient.updateOrderStatus(orderId, "REFUNDED");


        notificationServiceClient.sendNotification(response);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByOrderId(Long orderId) {
        log.info("Fetching all payments for Order ID: {}", orderId);
        return paymentRepository.findByOrderId(orderId)
                .stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }
}

