package com.ecom.payment.paymentservice.service;

import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
import com.ecom.payment.paymentservice.model.Payment;

import java.util.List;

public interface PaymentService {
    PaymentResponseDTO initiatePayment(PaymentRequestDTO requestDTO);
    PaymentResponseDTO updatePaymentStatus(Long paymentId, String status);
    PaymentResponseDTO getPaymentById(Long id);
    List<PaymentResponseDTO> getPaymentsByOrderId(String orderId);
    PaymentResponseDTO refundPayment(String orderId);
}
