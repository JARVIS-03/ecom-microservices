package com.ecom.payment.paymentservice.validator;

import static org.junit.jupiter.api.Assertions.*;
import com.ecom.payment.paymentservice.dto.*;
import com.ecom.payment.paymentservice.exception.PaymentProcessingException;
import org.junit.jupiter.api.Test;



class PaymentValidatorTest {

    @Test
    void testValidCreditCard() {
        CreditCardDTO cc = new CreditCardDTO();
        cc.setType("CREDIT_CARD");
        cc.setCardNumber("1234567812345678");
        cc.setExpiry("12/25");
        cc.setCvv("123");

        assertDoesNotThrow(() -> PaymentValidator.validate("CREDIT_CARD", cc));
    }

    @Test
    void testInvalidCreditCardNumber() {
        CreditCardDTO cc = new CreditCardDTO();
        cc.setType("CREDIT_CARD");
        cc.setCardNumber("12345678"); // Invalid: Too short
        cc.setExpiry("12/25");
        cc.setCvv("123");

        Exception exception = assertThrows(PaymentProcessingException.class, () -> PaymentValidator.validate("CREDIT_CARD", cc));
        assertEquals("Invalid Credit Card Number: Must be 16 digits", exception.getMessage());
    }

    @Test
    void testValidDebitCard() {
        DebitCardDTO dc = new DebitCardDTO();
        dc.setType("DEBIT_CARD");
        dc.setCardNumber("1234567812345678");
        dc.setExpiry("10/26");
        dc.setCvv("456");

        assertDoesNotThrow(() -> PaymentValidator.validate("DEBIT_CARD", dc));
    }

    @Test
    void testInvalidDebitCardExpiry() {
        DebitCardDTO dc = new DebitCardDTO();
        dc.setType("DEBIT_CARD");
        dc.setCardNumber("1234567812345678");
        dc.setExpiry("15/26"); // Invalid: Month should be between 01-12
        dc.setCvv("456");

        Exception exception = assertThrows(PaymentProcessingException.class, () -> PaymentValidator.validate("DEBIT_CARD", dc));
        assertEquals("Invalid Expiry Date: Must be in MM/YY format", exception.getMessage());
    }

    @Test
    void testValidMoneyTransfer() {
        MoneyTransferDTO mt = new MoneyTransferDTO();
        mt.setType("MONEY_TRANSFER");
        mt.setBankAccountNumber("123456789012");
        mt.setIfscCode("HDFC0001234");

        assertDoesNotThrow(() -> PaymentValidator.validate("MONEY_TRANSFER", mt));
    }

    @Test
    void testInvalidBankAccountNumber() {
        MoneyTransferDTO mt = new MoneyTransferDTO();
        mt.setType("MONEY_TRANSFER");
        mt.setBankAccountNumber("123"); // Invalid: Too short
        mt.setIfscCode("HDFC0001234");

        Exception exception = assertThrows(PaymentProcessingException.class, () -> PaymentValidator.validate("MONEY_TRANSFER", mt));
        assertEquals("Invalid Bank Account Number: Must be between 9-18 digits", exception.getMessage());
    }

    @Test
    void testValidPaypal() {
        PaypalDTO pp = new PaypalDTO();
        pp.setType("PAYPAL");
        pp.setPaypalEmail("user@example.com");

        assertDoesNotThrow(() -> PaymentValidator.validate("PAYPAL", pp));
    }

    @Test
    void testInvalidPaypalEmail() {
        PaypalDTO pp = new PaypalDTO();
        pp.setType("PAYPAL");
        pp.setPaypalEmail("invalid-email"); // Invalid format

        Exception exception = assertThrows(PaymentProcessingException.class, () -> PaymentValidator.validate("PAYPAL", pp));
        assertEquals("Invalid PayPal Email: Must be a valid email format", exception.getMessage());
    }
}