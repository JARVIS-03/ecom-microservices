package com.ecom.payment.paymentservice.service;

import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
import com.ecom.payment.paymentservice.model.Payment;
import com.ecom.payment.paymentservice.repository.PaymentRepository;
import com.ecom.payment.paymentservice.utillity.PaymentConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

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

        Payment payment = new Payment();
        payment.setOrderId(dto.getOrderId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus("INITIATED");
        payment.setDate(LocalDateTime.now());

        try {
            String methodDetails = objectMapper.writeValueAsString(dto.getMethodDetails());
            payment.setPaymentDetails(methodDetails);
        } catch (Exception e) {
            log.error("Error serializing method details", e);
        }

        payment = paymentRepository.save(payment);
        String result = mockPaymentGateway(dto.getPaymentMethod());
        log.info("Payment gateway result: {}", result);
        return updatePaymentStatus(payment.getPaymentId(), result);
    }

    private String mockPaymentGateway(String method) {
        return Math.random() > 0.5 ? "SUCCESS" : "FAILED";
    }

    public void updateOrderStatus(String orderId, String status) {
        String url = "http://ORDER-SERVICE/api/orders/" + orderId + "/status";
        restTemplate.put(url, status);
    }

    @Override
    public PaymentResponseDTO updatePaymentStatus(Long paymentId, String status) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow();
        payment.setStatus(status);
        payment = paymentRepository.save(payment);

//        updateOrderStatus(payment.getOrderId(), status);
        return paymentConverter.toDTO(payment);
    }

    @Override
    public PaymentResponseDTO getPaymentById(Long id) {
        return paymentConverter.toDTO(paymentRepository.findById(id).orElseThrow());
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .stream()
                .map(paymentConverter::toDTO)
                .collect(Collectors.toList());
    }
}

