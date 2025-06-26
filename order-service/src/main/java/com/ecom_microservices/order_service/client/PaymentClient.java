package com.ecom_microservices.order_service.client;

import com.ecom_microservices.order_service.dto.request.CreditCardDTO;
import com.ecom_microservices.order_service.dto.request.PaymentRequest;
import com.ecom_microservices.order_service.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentClient {

    private final RestTemplate restTemplate;

    public final String PAYMENT_SERVICE_URL="http://PAYMENTSERVICE/api/payments/initiate";

    public void sendPaymentNotification(Order savedOrder) {
        CreditCardDTO creditCardDTO  = CreditCardDTO.builder()
                .type("CREDIT_CARD")
                .cardNumber("4111111111111111")
                .expiry("12/26")
                .cvv("123")
                .build();

        PaymentRequest paymentRequest=PaymentRequest.builder()
                .orderId(savedOrder.getId())
                .paymentMethod("CREDIT_CARD")
                .amount(savedOrder.getTotalAmount())
                .email("mohan@gmail.com")
                .methodDetails(creditCardDTO)
                .build();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<PaymentRequest> request = new HttpEntity<>(paymentRequest, headers);

            restTemplate.postForLocation(PAYMENT_SERVICE_URL, request, savedOrder.getId());
            log.info("Payment sent successfully.");
        } catch (Exception e) {
            log.error("Failed to send Payment: {}", e.getMessage());
        }
    }
}
