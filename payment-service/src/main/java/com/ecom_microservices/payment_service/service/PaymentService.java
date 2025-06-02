package com.ecom_microservices.payment_service.service;


import com.ecom_microservices.payment_service.model.constants.PaymentStatus;
import com.ecom_microservices.payment_service.model.request.PaymentRequest;
import com.ecom_microservices.payment_service.model.request.PaymentStatusUpdate;
import com.ecom_microservices.payment_service.model.response.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponse initiatePayment(PaymentRequest request);

    PaymentResponse getPaymentById(UUID paymentId);

    PaymentResponse updatePaymentStatus(UUID paymentId, PaymentStatusUpdate update);

    List<PaymentResponse> getPaymentsByOrderId(UUID orderId);
}
