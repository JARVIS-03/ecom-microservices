package com.ecom.payment.paymentservice.utility;

import com.ecom.payment.paymentservice.dto.*;
import com.ecom.payment.paymentservice.model.Payment;
import com.ecom.payment.paymentservice.utillity.PaymentConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentConverterTest {

    private PaymentConverter converter;

    @BeforeEach
    void setup() {
        converter = new PaymentConverter();
    }

    @Test
    void testToDTO_withDebitCard() {
        String json = """
            {
              "type": "DEBIT_CARD",
              "cardNumber": "1111222233334444",
              "expiry": "12/26",
              "cvv": "123"
            }
        """;
        Payment payment = createPayment("DEBIT_CARD", json);

        PaymentResponseDTO dto = converter.toDTO(payment);

        assertNotNull(dto);
        assertEquals("DEBIT_CARD", dto.getPaymentMethod());
        assertTrue(dto.getMethodDetails() instanceof DebitCardDTO);
        DebitCardDTO details = (DebitCardDTO) dto.getMethodDetails();
        assertEquals("1111222233334444", details.getCardNumber());
        assertEquals("12/26", details.getExpiry());
        assertEquals("123", details.getCvv());
    }

    @Test
    void testToDTO_withCreditCard() {
        String json = """
            {
              "type": "CREDIT_CARD",
              "cardNumber": "5555666677778888",
              "expiry": "11/25",
              "cvv": "456"
            }
        """;
        Payment payment = createPayment("CREDIT_CARD", json);

        PaymentResponseDTO dto = converter.toDTO(payment);

        assertNotNull(dto);
        assertTrue(dto.getMethodDetails() instanceof CreditCardDTO);
        CreditCardDTO details = (CreditCardDTO) dto.getMethodDetails();
        assertEquals("5555666677778888", details.getCardNumber());
        assertEquals("11/25", details.getExpiry());
        assertEquals("456", details.getCvv());
    }

    @Test
    void testToDTO_withPaypal() {
        String json = """
            {
              "type": "PAYPAL",
              "paypalEmail": "user@paypal.com"
            }
        """;
        Payment payment = createPayment("PAYPAL", json);

        PaymentResponseDTO dto = converter.toDTO(payment);

        assertNotNull(dto);
        assertTrue(dto.getMethodDetails() instanceof PaypalDTO);
        PaypalDTO details = (PaypalDTO) dto.getMethodDetails();
        assertEquals("user@paypal.com", details.getPaypalEmail());
    }

    @Test
    void testToDTO_withMoneyTransfer() {
        String json = """
            {
              "type": "MONEY_TRANSFER",
              "bankAccountNumber": "1234567890",
              "ifscCode": "IFSC0012345"
            }
        """;
        Payment payment = createPayment("MONEY_TRANSFER", json);

        PaymentResponseDTO dto = converter.toDTO(payment);

        assertNotNull(dto);
        assertTrue(dto.getMethodDetails() instanceof MoneyTransferDTO);
        MoneyTransferDTO details = (MoneyTransferDTO) dto.getMethodDetails();
        assertEquals("1234567890", details.getBankAccountNumber());
        assertEquals("IFSC0012345", details.getIfscCode());
    }

    @Test
    void testToDTO_withInvalidPaymentMethod() {
        String json = """
            {
              "type": "UNKNOWN",
              "someField": "value"
            }
        """;
        Payment payment = createPayment("UNKNOWN", json);

        PaymentResponseDTO dto = converter.toDTO(payment);

        assertNotNull(dto);
        assertNull(dto.getMethodDetails()); // Unknown type should not deserialize
    }

    @Test
    void testToDTO_withMalformedJson() {
        String json = "{ type: 'DEBIT_CARD', cardNumber: }";  // Invalid JSON
        Payment payment = createPayment("DEBIT_CARD", json);

        PaymentResponseDTO dto = converter.toDTO(payment);

        assertNotNull(dto);
        assertNull(dto.getMethodDetails()); // Should gracefully handle exception
    }

    // Helper method
    private Payment createPayment(String method, String detailsJson) {
        Payment payment = new Payment();
        payment.setPaymentId(1L);
        payment.setOrderId("ORD123");
        payment.setAmount(250.75);
        payment.setStatus("SUCCESS");
        payment.setPaymentMethod(method);
        payment.setDate(LocalDateTime.now());
        payment.setPaymentDetails(detailsJson);
        return payment;
    }
}
