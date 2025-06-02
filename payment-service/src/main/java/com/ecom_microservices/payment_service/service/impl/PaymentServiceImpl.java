package com.ecom_microservices.payment_service.service.impl;

import com.ecom_microservices.payment_service.exception.PaymentNotFoundException;
import com.ecom_microservices.payment_service.model.Payment;
import com.ecom_microservices.payment_service.model.constants.PaymentStatus;
import com.ecom_microservices.payment_service.model.request.PaymentRequest;
import com.ecom_microservices.payment_service.model.request.PaymentStatusUpdate;
import com.ecom_microservices.payment_service.model.response.PaymentResponse;
import com.ecom_microservices.payment_service.repository.PaymentRepository;
import com.ecom_microservices.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    @Retryable(maxAttempts = 3)
    public PaymentResponse initiatePayment(PaymentRequest request) {
        log.info("Initiating payment for orderId: {}", request.getOrderId());

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.INITIATED)
                .transactionReference("TXN-" + UUID.randomUUID().toString().substring(0, 8))
                .build();

        Payment saved = paymentRepository.save(payment);
        return mapToDTO(saved);
    }

    @Override
    public PaymentResponse getPaymentById(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment ID not found: " + paymentId));
        return mapToDTO(payment);
    }

    @Override
    public PaymentResponse updatePaymentStatus(UUID paymentId, PaymentStatusUpdate update) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment ID not found: " + paymentId));

        log.info("Updating payment {} status to {}", paymentId, update.getStatus());
        payment.setStatus(update.getStatus());

        Payment updated = paymentRepository.save(payment);
        return mapToDTO(updated);
    }

    @Override
    public List<PaymentResponse> getPaymentsByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToDTO(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionReference(payment.getTransactionReference())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
