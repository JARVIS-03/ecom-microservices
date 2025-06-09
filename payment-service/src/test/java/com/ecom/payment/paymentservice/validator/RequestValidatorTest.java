package com.ecom.payment.paymentservice.validator;

import com.ecom.payment.paymentservice.dto.*;
import com.ecom.payment.paymentservice.exception.PaymentException;
import com.ecom.payment.paymentservice.model.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RequestValidatorTest {

    @Test
    void testValidateRequestParam_NullOrBlank_Throws() {
        PaymentException ex = assertThrows(PaymentException.class, () -> {
            RequestValidator.validateRequestParam(null);
        });
        assertEquals(ErrorCode.PAYMENT_VALIDATION_FAILED, ex.getErrorCode());

        ex = assertThrows(PaymentException.class, () -> {
            RequestValidator.validateRequestParam("");
        });
        assertEquals(ErrorCode.PAYMENT_VALIDATION_FAILED, ex.getErrorCode());

        ex = assertThrows(PaymentException.class, () -> {
            RequestValidator.validateRequestParam("   ");
        });
        assertEquals(ErrorCode.PAYMENT_VALIDATION_FAILED, ex.getErrorCode());
    }

    @Test
    void testValidateRequestParam_ExceedsMaxLength_Throws() {
        String value = "12345";
        // Should pass because length = 5, maxAllowedLimit = 5
        assertDoesNotThrow(() -> RequestValidator.validateRequestParam(value, 5));

        // Should throw because length > 5
        PaymentException ex = assertThrows(PaymentException.class, () -> {
            RequestValidator.validateRequestParam(value, 4);
        });
        assertEquals(ErrorCode.PAYMENT_VALIDATION_FAILED, ex.getErrorCode());
    }

    @Test
    void testValidatePaymentDetails_NullRequest_Throws() {
        PaymentException ex = assertThrows(PaymentException.class, () -> {
            RequestValidator.validatePaymentDetails(null);
        });
        assertEquals(ErrorCode.PAYMENT_VALIDATION_FAILED, ex.getErrorCode());
    }

    @Test
    void testValidatePaymentDetails_InvalidPaymentMethodDetails_Throws() {
        // Use a dummy PaymentRequestDTO with invalid payment method to trigger failure
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setPaymentMethod("INVALID_METHOD");
        // create dummy PaymentMethodDetails to satisfy signature
        request.setMethodDetails(new PaymentMethodDetails() {
            @Override
            public String getType() {
                return null;
            }

            @Override
            public void setType(String type) {

            }
        });

        PaymentException ex = assertThrows(PaymentException.class, () -> {
            RequestValidator.validatePaymentDetails(request);
        });
        assertEquals(ErrorCode.PAYMENT_UNSUPPORTED_METHOD, ex.getErrorCode());
    }

    @Test
    void testValidatePaymentDetails_ValidPaymentMethodDetails_ReturnsTrue() {
        // Setup a valid CreditCardDTO for testing
        CreditCardDTO cc = new CreditCardDTO();
        cc.setCardNumber("1234567812345678");
        cc.setExpiry("12/25");
        cc.setCvv("123");

        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setPaymentMethod("CREDIT_CARD");
        request.setMethodDetails(cc);

        assertDoesNotThrow(() -> RequestValidator.validatePaymentDetails(request));
    }
}
