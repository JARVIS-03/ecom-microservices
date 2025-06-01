package com.ecom_microservices.payment_service.service;


import com.ecom_microservices.payment_service.model.constants.PaymentStatus;
import com.ecom_microservices.payment_service.model.request.PaymentRequest;
import com.ecom_microservices.payment_service.model.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse initiatePayment(PaymentRequest request);

    PaymentResponse getPayment(Long id);

    List<PaymentResponse> listByOrderId(String orderId);

    void updateStatus(Long id, PaymentStatus newStatus);
}