package com.ecom.payment.paymentservice.validator;

import com.ecom.payment.paymentservice.dto.*;
import com.ecom.payment.paymentservice.exception.PaymentException;
import com.ecom.payment.paymentservice.exception.model.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentValidatorTest {

    @Test
    void testValidateCreditCard_Success() {
        CreditCardDTO cc = new CreditCardDTO();
        cc.setCardNumber("1234567812345678");
        cc.setExpiry("12/25");
        cc.setCvv("123");

        assertTrue(PaymentValidator.validate("CREDIT_CARD", cc));
    }

    @Test
    void testValidateCreditCard_InvalidCardNumber() {
        CreditCardDTO cc = new CreditCardDTO();
        cc.setCardNumber("1234");
        cc.setExpiry("12/25");
        cc.setCvv("123");

        PaymentException ex = assertThrows(PaymentException.class,
                () -> PaymentValidator.validate("CREDIT_CARD", cc));
        assertEquals(ErrorCode.CREDITCARD_NUMBER_INVALID, ex.getErrorCode());
    }

    @Test
    void testValidateCreditCard_InvalidExpiry() {
        CreditCardDTO cc = new CreditCardDTO();
        cc.setCardNumber("1234567812345678");
        cc.setExpiry("13/25");
        cc.setCvv("123");

        PaymentException ex = assertThrows(PaymentException.class,
                () -> PaymentValidator.validate("CREDIT_CARD", cc));
        assertEquals(ErrorCode.CREDITCARD_EXPIRY_INVALID, ex.getErrorCode());
    }

    @Test
    void testValidateCreditCard_InvalidCvv() {
        CreditCardDTO cc = new CreditCardDTO();
        cc.setCardNumber("1234567812345678");
        cc.setExpiry("12/25");
        cc.setCvv("12");

        PaymentException ex = assertThrows(PaymentException.class,
                () -> PaymentValidator.validate("CREDIT_CARD", cc));
        assertEquals(ErrorCode.CREDITCARD_CVV_INVALID, ex.getErrorCode());
    }

    @Test
    void testValidateDebitCard_Success() {
        DebitCardDTO dc = new DebitCardDTO();
        dc.setCardNumber("8765432187654321");
        dc.setExpiry("11/24");
        dc.setCvv("321");

        assertTrue(PaymentValidator.validate("DEBIT_CARD", dc));
    }

    @Test
    void testValidateDebitCard_InvalidCardNumber() {
        DebitCardDTO dc = new DebitCardDTO();
        dc.setCardNumber("8765");
        dc.setExpiry("11/24");
        dc.setCvv("321");

        PaymentException ex = assertThrows(PaymentException.class,
                () -> PaymentValidator.validate("DEBIT_CARD", dc));
        assertEquals(ErrorCode.DEBITCARD_NUMBER_INVALID, ex.getErrorCode());
    }

    @Test
    void testValidateDebitCard_InvalidExpiry() {
        DebitCardDTO dc = new DebitCardDTO();
        dc.setCardNumber("8765432187654321");
        dc.setExpiry("00/24");
        dc.setCvv("321");

        PaymentException ex = assertThrows(PaymentException.class,
                () -> PaymentValidator.validate("DEBIT_CARD", dc));
        assertEquals(ErrorCode.DEBITCARD_EXPIRY_INVALID, ex.getErrorCode());
    }

    @Test
    void testValidateDebitCard_InvalidCvv() {
        DebitCardDTO dc = new DebitCardDTO();
        dc.setCardNumber("8765432187654321");
        dc.setExpiry("11/24");
        dc.setCvv("3");

        PaymentException ex = assertThrows(PaymentException.class,
                () -> PaymentValidator.validate("DEBIT_CARD", dc));
        assertEquals(ErrorCode.DEBITCARD_CVV_INVALID, ex.getErrorCode());
    }

    @Test
    void testValidateMoneyTransfer_Success() {
        MoneyTransferDTO mt = new MoneyTransferDTO();
        mt.setBankAccountNumber("123456789012");
        mt.setIfscCode("ABCD1234567");

        assertTrue(PaymentValidator.validate("MONEY_TRANSFER", mt));
    }

    @Test
    void testValidateMoneyTransfer_InvalidAccountNumber() {
        MoneyTransferDTO mt = new MoneyTransferDTO();
        mt.setBankAccountNumber("1234");
        mt.setIfscCode("ABCD1234567");

        PaymentException ex = assertThrows(PaymentException.class,
                () -> PaymentValidator.validate("MONEY_TRANSFER", mt));
        assertEquals(ErrorCode.MONEYTRANSFER_ACCOUNT_INVALID, ex.getErrorCode());
    }

    @Test
    void testValidateMoneyTransfer_InvalidIfscCode() {
        MoneyTransferDTO mt = new MoneyTransferDTO();
        mt.setBankAccountNumber("123456789012");
        mt.setIfscCode("abcd1234567");

        PaymentException ex = assertThrows(PaymentException.class,
                () -> PaymentValidator.validate("MONEY_TRANSFER", mt));
        assertEquals(ErrorCode.MONEYTRANSFER_IFSC_INVALID, ex.getErrorCode());
    }

    @Test
    void testValidatePaypal_Success() {
        PaypalDTO pp = new PaypalDTO();
        pp.setPaypalEmail("test@example.com");

        assertTrue(PaymentValidator.validate("PAYPAL", pp));
    }

    @Test
    void testValidatePaypal_InvalidEmail() {
        PaypalDTO pp = new PaypalDTO();
        pp.setPaypalEmail("invalid-email");

        PaymentException ex = assertThrows(PaymentException.class,
                () -> PaymentValidator.validate("PAYPAL", pp));
        assertEquals(ErrorCode.PAYPAL_EMAIL_INVALID, ex.getErrorCode());
    }

    @Test
    void testValidate_UnsupportedPaymentMethod() {
        PaymentMethodDetails dummyDetails = new PaymentMethodDetails() {
            @Override
            public String getType() {
                return null;
            }

            @Override
            public void setType(String type) {

            }
        }; // anonymous subclass

        PaymentException ex = assertThrows(PaymentException.class,
                () -> PaymentValidator.validate("UNKNOWN_METHOD", dummyDetails));
        assertEquals(ErrorCode.PAYMENT_UNSUPPORTED_METHOD, ex.getErrorCode());
    }
}
