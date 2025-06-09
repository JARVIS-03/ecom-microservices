package com.ecom.payment.paymentservice.service.impl;

import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
import com.ecom.payment.paymentservice.enums.PaymentStatus;
import com.ecom.payment.paymentservice.exception.PaymentException;
import com.ecom.payment.paymentservice.exception.model.ErrorCode;
import com.ecom.payment.paymentservice.mapper.PaymentRequestDTOtoPaymentMapper;
import com.ecom.payment.paymentservice.model.Payment;
import com.ecom.payment.paymentservice.repository.PaymentRepository;
import com.ecom.payment.paymentservice.service.PaymentService;
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

        Payment payment = PaymentRequestDTOtoPaymentMapper.INSTANCE.map(dto);
        payment.setStatus(PaymentStatus.INITIATED);

        try {
            String methodDetails = objectMapper.writeValueAsString(dto.getMethodDetails());
            payment.setPaymentDetails(methodDetails);
        } catch (Exception e) {
            log.error("Error serializing method details", e);
            throw new PaymentException(ErrorCode.PAYMENT_INTERNAL_ERROR);
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
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

        PaymentStatus paymentStatus = mapStatus(status);
        payment.setStatus(paymentStatus);

        payment = paymentRepository.save(payment);
        return paymentConverter.toDTO(payment);
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
    public PaymentResponseDTO getPaymentById(Long id) {
        return paymentConverter.toDTO(paymentRepository.findById(id).orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND)));
    }



    @Override
    public PaymentResponseDTO refundPayment(Long orderId) {
        return null;
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .stream()
                .map(paymentConverter::toDTO)
                .collect(Collectors.toList());
    }
}

