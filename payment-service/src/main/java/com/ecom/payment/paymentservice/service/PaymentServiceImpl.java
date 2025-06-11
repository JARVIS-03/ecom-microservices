package com.ecom.payment.paymentservice.service;

import com.ecom.payment.paymentservice.dto.NotificationDTO;
import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
import com.ecom.payment.paymentservice.enums.PaymentStatus;
import com.ecom.payment.paymentservice.exception.InvalidOrderException;
import com.ecom.payment.paymentservice.exception.ResourceNotFoundException;
import com.ecom.payment.paymentservice.mapper.PaymentRequestDTOtoPaymentMapper;
import com.ecom.payment.paymentservice.model.Payment;
import com.ecom.payment.paymentservice.repository.PaymentRepository;
import com.ecom.payment.paymentservice.utillity.PaymentConverter;
import com.ecom.payment.paymentservice.utillity.PaymentGatewaySimulator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
//    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
//
////    private final PaymentRepository paymentRepository;
////    private final RestTemplate restTemplate;
////    private final PaymentConverter paymentConverter;
//    @Autowired
//    private PaymentRepository paymentRepository;
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Autowired
//    private PaymentConverter paymentConverter;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    private static final String ORDER_SERVICE_BASE_URL = "http://ORDER-SERVICE/api/orders/";
//    private static final String NOTIFICATION_SERVICE_BASE_URL = "http://localhost:8081/api/notifications/payment/send";
//
//    @Override
//    @Transactional
//    public PaymentResponseDTO initiatePayment(PaymentRequestDTO dto) {
//        log.info("Initiating payment for orderId: {}", dto.getOrderId());
//
////        validateOrder(dto.getOrderId());
////        boolean hasSuccessfulPayment = paymentRepository.findByOrderId(dto.getOrderId()).stream()
////               .anyMatch(payment -> PaymentStatus.SUCCESS.equals(payment.getStatus()));
//        boolean hasSuccessfulPayment = false;
//
//
//        if (hasSuccessfulPayment) {
//            log.warn("Payment already completed for orderId: {}", dto.getOrderId());
//            throw new IllegalStateException("Payment already completed for order ID: " + dto.getOrderId());
//        }
//
//        Payment payment = buildPaymentEntity(dto);
//        payment = paymentRepository.save(payment);
//        log.info("Payment saved with ID: {}", payment.getPaymentId());
//
//        String result = PaymentGatewaySimulator.simulate(dto.getPaymentMethod());
//        log.info("Payment gateway result for method {}: {}", dto.getPaymentMethod(), result);
//
//        PaymentResponseDTO response = updatePaymentStatus(payment.getPaymentId(), result);
//        log.info("Final payment response after status update: {}", response);
//        sendNotification(response, "");
//
//        return response;
//    }
//
//    private Payment buildPaymentEntity(PaymentRequestDTO dto) {
//
//        // Should be changed using ModelMapper
//        Payment payment = new Payment();
//        payment.setOrderId(dto.getOrderId());
//        payment.setAmount(dto.getAmount());
//        payment.setPaymentMethod(dto.getPaymentMethod());
//        payment.setStatus(PaymentStatus.INITIATED);
//        payment.setDate(LocalDateTime.now());
//
//        try {
//            String methodDetails = objectMapper.writeValueAsString(dto.getMethodDetails());
//            payment.setPaymentDetails(methodDetails);
//            log.debug("Serialized method details: {}", methodDetails);
//        } catch (JsonProcessingException e) {
//            log.error("Error serializing method details", e);
//            throw new IllegalStateException("Failed to serialize payment method details");
//        }
//
//        return payment;
//    }
//
////    private void validateOrder(Long orderId) {
////        try {
////            restTemplate.getForObject(ORDER_SERVICE_BASE_URL + orderId, Object.class);
////            log.info("Order ID {} is valid", orderId);
////        }
////        catch (Exception ex) {
////            log.error("Invalid order ID: {}", orderId, ex);
////            throw new InvalidOrderException("Invalid order ID: "+orderId);
////        }
////    }
//@CircuitBreaker(name = "orderService", fallbackMethod = "orderFallback")
//@Retry(name = "orderService")
//private void validateOrder(Long orderId) {
//    Object orderResponse = restTemplate.getForObject(ORDER_SERVICE_BASE_URL + orderId, Object.class);
//
//    if (orderResponse == null) {
//        log.error("Order not found for orderId: {}", orderId);
//        throw new InvalidOrderException("Invalid order ID: " + orderId);
//    }
//
//    log.info("Order ID {} is valid", orderId);
//}
//
//    private void orderFallback(Long orderId, Throwable t) {
//        log.error("Order service fallback triggered for orderId: {}", orderId, t);
//
//        // If you want to throw a specific exception to upper layers:
//        throw new InvalidOrderException("Order service is down or unavailable for orderId: " + orderId);
//
//        // Or if you want to return a custom response or wrap the exception differently,
//        // you can do it here instead.
//    }
//
//    @Override
//    @Transactional
//    public PaymentResponseDTO updatePaymentStatus(Long paymentId, String status) {
//        log.info("Updating payment status for ID: {} to {}", paymentId, status);
//
//        Payment payment = paymentRepository.findById(paymentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for ID: " + paymentId));
//
//        PaymentStatus parsedStatus = parsedPaymentStatus(status);
//        payment.setStatus(parsedStatus);
//
//        return paymentConverter.toDTO(paymentRepository.save(payment));
//    }
//
//    private PaymentStatus parsedPaymentStatus(String status) {
//        try {
//            return PaymentStatus.valueOf(status.toUpperCase());
//        }
//        catch (IllegalArgumentException e) {
//            throw new IllegalStateException("Invalid Payment Status: "+status);
//        }
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public PaymentResponseDTO getPaymentById(Long id) {
//        log.info("Fetching payment by ID: {}", id);
//
//        Payment payment = paymentRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for ID: " + id));
//
//        return paymentConverter.toDTO(payment);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<PaymentResponseDTO> getPaymentsByOrderId(Long orderId) {
//        log.info("Fetching all payments for Order ID: {}", orderId);
//
//        return paymentRepository.findByOrderId(orderId).stream()
//                .map(paymentConverter::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional
//    public PaymentResponseDTO refundPayment(Long orderId) {
//        log.info("Initiating refund for orderId: {}", orderId);
//        validateOrder(orderId);
//        List<Payment> payments = paymentRepository.findByOrderId(orderId);
//        if (payments.isEmpty()) {
//            throw new ResourceNotFoundException("No payments found for Order ID: " + orderId);
//        }
//
//        Payment successfulPayment = payments.stream()
//                .filter(p -> PaymentStatus.SUCCESS.equals(p.getStatus()))
//                .findFirst()
//                .orElseThrow(() ->
//                        new ResourceNotFoundException("No successful payment found to refund for Order ID: " + orderId)
//                );
//
//
//        successfulPayment.setStatus(PaymentStatus.REFUNDED);
//        PaymentResponseDTO response = paymentConverter.toDTO(paymentRepository.save(successfulPayment));
//        log.info("Payment ID {} marked as REFUNDED", successfulPayment.getPaymentId());
//
//        updateOrderStatus(orderId, "REFUNDED");
//        sendNotification(response, "vamsikrishnamavilla@gmail.com");
//
//        return paymentConverter.toDTO(successfulPayment);
//    }
//
////    private void updateOrderStatus(Long orderId, String status) {
////        try {
////            restTemplate.put(ORDER_SERVICE_BASE_URL + orderId + "/status", status);
////            log.info("Order status updated for orderId: {} with status: {}", orderId, status);
////        } catch (Exception e) {
////            log.error("Error updating order status for orderId: {}", orderId, e);
////            throw new RuntimeException("Failed to update order status for orderId: " + orderId);
////        }
////    }
//
//    @CircuitBreaker(name = "orderService", fallbackMethod = "updateOrderStatusFallback")
//    @Retry(name = "orderService")
//    private void updateOrderStatus(Long orderId, String status) {
//        restTemplate.put(ORDER_SERVICE_BASE_URL + orderId + "/status", status);
//        log.info("Order status updated for orderId: {} with status: {}", orderId, status);
//    }
//    private void updateOrderStatusFallback(Long orderId, String status, Throwable t) {
//        log.error("Fallback triggered while updating order status for orderId: {}, status: {}", orderId, status, t);
//        throw new RuntimeException("OrderServiceDown: Unable to update order status for orderId: " + orderId);
//    }
////    private void sendNotification(PaymentResponseDTO response, String userEmail) {
////        NotificationDTO notificationDTO = new NotificationDTO(
////                userEmail,
////                response.getPaymentId(),
////                response.getStatus()
////        );
////
////        try {
////            restTemplate.postForObject(NOTIFICATION_SERVICE_BASE_URL, notificationDTO, Void.class);
////            log.info("Notification sent to user: {}", userEmail);
////        } catch (Exception e) {
////            log.error("Failed to send notification to user: {}", userEmail, e);
////        }
////    }
//    @CircuitBreaker(name = "notificationService", fallbackMethod = "notificationFallback")
//    @Retry(name = "notificationService")
//    private void sendNotification(PaymentResponseDTO response, String userEmail) {
//        NotificationDTO notificationDTO = new NotificationDTO(
//                userEmail,
//                response.getPaymentId(),
//                response.getStatus()
//        );
//
//        restTemplate.postForObject(NOTIFICATION_SERVICE_BASE_URL, notificationDTO, Void.class);
//        log.info("Notification sent to user: {}", userEmail);
//    }
//
//    private void notificationFallback(PaymentResponseDTO response, String userEmail, Throwable t) {
//        log.error("Notification service fallback triggered for user: {}", userEmail, t);
//        throw new RuntimeException("NotificationServiceDown: Unable to send notification to " + userEmail);
//    }
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
            throw new IllegalStateException("Payment already completed for order ID: " + dto.getOrderId());
        }

        Payment payment = buildPaymentEntity(dto);
        payment = paymentRepository.save(payment);
        log.info("Payment saved with ID: {}", payment.getPaymentId());

        String result = PaymentGatewaySimulator.simulate(dto.getPaymentMethod());
        log.info("Payment gateway result for method {}: {}", dto.getPaymentMethod(), result);

        PaymentResponseDTO response = updatePaymentStatus(payment.getPaymentId(), result);
        log.info("Final payment response after status update: {}", response);

        notificationServiceClient.sendNotification(response); // Provide email as needed

        return response;
    }

    private Payment buildPaymentEntity(PaymentRequestDTO dto) {
        Payment payment = new Payment();
        payment.setOrderId(dto.getOrderId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(PaymentStatus.INITIATED);
        payment.setDate(LocalDateTime.now());
        payment.setEmail(dto.getEmail());

        try {
            String methodDetails = objectMapper.writeValueAsString(dto.getMethodDetails());
            payment.setPaymentDetails(methodDetails);
            log.debug("Serialized method details: {}", methodDetails);
        } catch (JsonProcessingException e) {
            log.error("Error serializing method details", e);
            throw new IllegalStateException("Failed to serialize payment method details");
        }

        return payment;
    }

    @Override
    @Transactional
    public PaymentResponseDTO updatePaymentStatus(Long paymentId, String status) {
        log.info("Updating payment status for ID: {} to {}", paymentId, status);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for ID: " + paymentId));

        PaymentStatus parsedStatus = parsedPaymentStatus(status);
        payment.setStatus(parsedStatus);

        return paymentConverter.toDTO(paymentRepository.save(payment));
    }

    private PaymentStatus parsedPaymentStatus(String status) {
        try {
            return PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid Payment Status: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        log.info("Fetching payment by ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for ID: " + id));

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
            throw new ResourceNotFoundException("No payments found for Order ID: " + orderId);
        }

        Payment successfulPayment = payments.stream()
                .filter(p -> PaymentStatus.SUCCESS.equals(p.getStatus()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No successful payment found to refund for Order ID: " + orderId));

        successfulPayment.setStatus(PaymentStatus.REFUNDED);
        PaymentResponseDTO response = paymentConverter.toDTO(paymentRepository.save(successfulPayment));
        log.info("Payment ID {} marked as REFUNDED", successfulPayment.getPaymentId());

        orderServiceClient.updateOrderStatus(orderId, "REFUNDED");

        notificationServiceClient.sendNotification(response);

        return response;
    }


}

