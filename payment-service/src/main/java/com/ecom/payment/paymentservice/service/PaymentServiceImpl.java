package com.ecom.payment.paymentservice.service;

import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
import com.ecom.payment.paymentservice.exception.InvalidOrderException;
import com.ecom.payment.paymentservice.exception.ResourceNotFoundException;
import com.ecom.payment.paymentservice.mapper.PaymentRequestDTOtoPaymentMapper;
import com.ecom.payment.paymentservice.model.Payment;
import com.ecom.payment.paymentservice.repository.PaymentRepository;
import com.ecom.payment.paymentservice.utillity.PaymentConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PaymentConverter paymentConverter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PaymentResponseDTO initiatePayment(PaymentRequestDTO dto) {
        log.info("Initiating payment for orderId: {}", dto.getOrderId());

//        String orderServiceUrl = "http://ORDER-SERVICE/api/orders/" + dto.getOrderId();
//        try {
//            restTemplate.getForObject(orderServiceUrl, Object.class);
//            log.info("Order ID {} is valid", dto.getOrderId());
//        } catch (Exception ex) {
//            log.error("Invalid order ID: {}", dto.getOrderId(), ex);
//            throw new InvalidOrderException("Invalid Order ID: " + dto.getOrderId());
//        }
//
//        boolean hasSuccessfulPayment = paymentRepository.findByOrderId(dto.getOrderId()).stream()
//                .anyMatch(payment -> "SUCCESS".equalsIgnoreCase(payment.getStatus()));
        boolean hasSuccessfulPayment=false;

        if (hasSuccessfulPayment) {
            log.warn("Payment already completed for orderId: {}", dto.getOrderId());
            throw new RuntimeException("Payment already completed for order ID: " + dto.getOrderId());
        }

        Payment payment = new Payment();
        payment.setOrderId(dto.getOrderId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus("INITIATED");
        payment.setDate(LocalDateTime.now());

        String methodDetails;
        try {
            methodDetails = objectMapper.writeValueAsString(dto.getMethodDetails());
            payment.setPaymentDetails(methodDetails);
            log.debug("Serialized method details: {}", methodDetails);
        } catch (Exception e) {
            log.error("Error serializing method details", e);
            throw new RuntimeException("Failed to serialize payment method details");
        }

        payment = paymentRepository.save(payment);
        log.info("Payment saved with ID: {}", payment.getPaymentId());

        String result = mockPaymentGateway(dto.getPaymentMethod());
        log.info("Payment gateway result for method {}: {}", dto.getPaymentMethod(), result);

        PaymentResponseDTO response = updatePaymentStatus(payment.getPaymentId(), result);
        log.info("Final payment response after status update: {}", response);
        return response;
    }

    private String mockPaymentGateway(String method) {
        String status = Math.random() > 0.2 ? "SUCCESS" : "FAILED";
        log.debug("Mock payment gateway status for method {}: {}", method, status);
        return status;
    }

    public void updateOrderStatus(String orderId, String status) {
        String url = "http://ORDER-SERVICE/api/orders/" + orderId + "/status";
        try {
            restTemplate.put(url, status);
            log.info("Order status updated for orderId: {} with status: {}", orderId, status);
        } catch (Exception e) {
            log.error("Error updating order status for orderId: {}", orderId, e);
            throw new RuntimeException("Failed to update order status for orderId: " + orderId);
        }
    }
    @Override
    public PaymentResponseDTO updatePaymentStatus(Long paymentId, String status) {
        log.info("Updating payment status for ID: {} to {}", paymentId, status);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for ID: " + paymentId));

        payment.setStatus(status);
        payment = paymentRepository.save(payment);

        return paymentConverter.toDTO(payment);
    }

    @Override
    public PaymentResponseDTO getPaymentById(Long id) {
        log.info("Fetching payment by ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for ID: " + id));

        return paymentConverter.toDTO(payment);
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .stream()
                .map(paymentConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponseDTO refundPayment(String orderId) {
        log.info("Initiating refund for orderId: {}", orderId);

        String orderServiceUrl = "http://ORDER-SERVICE/api/orders/" + orderId;
        try {
            restTemplate.getForObject(orderServiceUrl, Object.class);
            log.info("Order ID {} is valid", orderId);
        } catch (Exception ex) {
            log.error("Invalid order ID: {}", orderId, ex);
            throw new InvalidOrderException("Invalid Order ID: " + orderId);
        }

        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        if (payments.isEmpty()) {
            throw new ResourceNotFoundException("No payments found for Order ID: " + orderId);
        }

        Payment successfulPayment = payments.stream()
                .filter(p -> "SUCCESS".equalsIgnoreCase(p.getStatus()))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("No successful payment found to refund for Order ID: " + orderId)
                );


        successfulPayment.setStatus("REFUNDED");
        paymentRepository.save(successfulPayment);
        log.info("Payment ID {} marked as REFUNDED", successfulPayment.getPaymentId());

        updateOrderStatus(orderId, "REFUNDED");

        return paymentConverter.toDTO(successfulPayment);
    }
}

