//package com.ecom.payment.paymentservice.validator;
//
//import com.ecom.payment.paymentservice.dto.*;
//import com.ecom.payment.paymentservice.exception.PaymentProcessingException;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
//class RequestValidatorTest {
//
//    @Test
//    void testValidRequestParam() {
//        assertDoesNotThrow(() -> RequestValidator.validateRequestParam("ValidParam"));
//    }
//
//    @Test
//    void testInvalidRequestParam_BlankValue() {
//        Exception exception = assertThrows(PaymentProcessingException.class, () -> RequestValidator.validateRequestParam(""));
//        assertEquals("Invalid Request", exception.getMessage());
//    }
//
//    @Test
//    void testInvalidRequestParam_NullValue() {
//        Exception exception = assertThrows(PaymentProcessingException.class, () -> RequestValidator.validateRequestParam(null));
//        assertEquals("Invalid Request", exception.getMessage());
//    }
//
//    @Test
//    void testValidRequestParam_WithMaxLimit() {
//        assertDoesNotThrow(() -> RequestValidator.validateRequestParam("ValidLength", 20));
//    }
//
//    @Test
//    void testInvalidRequestParam_ExceedsMaxLimit() {
//        Exception exception = assertThrows(PaymentProcessingException.class, () -> RequestValidator.validateRequestParam("ExceedingMaxLength", 10));
//        assertEquals("Invalid Request", exception.getMessage());
//    }
//
//    @Test
//    void testValidPaymentDetails() {
//        PaymentRequestDTO request = new PaymentRequestDTO();
//        request.setOrderId("123");
//        request.setAmount(100.0);
//        request.setPaymentMethod("CREDIT_CARD");
//
//        CreditCardDTO cc = new CreditCardDTO();
//        cc.setType("CREDIT_CARD");
//        cc.setCardNumber("1234567812345678");
//        cc.setExpiry("12/25");
//        cc.setCvv("123");
//
//        request.setMethodDetails(cc);
//
//        assertDoesNotThrow(() -> RequestValidator.validatePaymentDetails(request));
//    }
//
//    @Test
//    void testInvalidPaymentDetails_NullRequest() {
//        Exception exception = assertThrows(PaymentProcessingException.class, () -> RequestValidator.validatePaymentDetails(null));
//        assertEquals("Invalid Request", exception.getMessage());
//    }
//
//    @Test
//    void testInvalidPaymentDetails_BadPaymentMethod() {
//        PaymentRequestDTO request = new PaymentRequestDTO();
//        request.setOrderId("123");
//        request.setAmount(100.0);
//        request.setPaymentMethod("INVALID_METHOD"); // Unsupported Payment Method
//
//        Exception exception = assertThrows(PaymentProcessingException.class, () -> RequestValidator.validatePaymentDetails(request));
//        assertEquals("Unsupported payment method: INVALID_METHOD", exception.getMessage());
//    }
//}