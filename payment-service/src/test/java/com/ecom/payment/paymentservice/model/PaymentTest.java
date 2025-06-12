//package com.ecom.payment.paymentservice.model;
//
//import org.junit.jupiter.api.Test;
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class PaymentTest {
//
//    @Test
//    void testPaymentEntityGettersAndSetters() {
//        Payment payment = new Payment();
//
//        Long paymentId = 1L;
//        String orderId = "ORD123";
//        Double amount = 250.0;
//        String status = "SUCCESS";
//        String paymentMethod = "Credit Card";
//        LocalDateTime now = LocalDateTime.now();
//        String paymentDetails = "{\"cardType\":\"Visa\"}";
//
//        payment.setPaymentId(paymentId);
//        payment.setOrderId(orderId);
//        payment.setAmount(amount);
//        payment.setStatus(status);
//        payment.setPaymentMethod(paymentMethod);
//        payment.setDate(now);
//        payment.setPaymentDetails(paymentDetails);
//
//        assertEquals(paymentId, payment.getPaymentId());
//        assertEquals(orderId, payment.getOrderId());
//        assertEquals(amount, payment.getAmount());
//        assertEquals(status, payment.getStatus());
//        assertEquals(paymentMethod, payment.getPaymentMethod());
//        assertEquals(now, payment.getDate());
//        assertEquals(paymentDetails, payment.getPaymentDetails());
//    }
//}
