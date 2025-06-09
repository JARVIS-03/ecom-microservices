package com.ecom.payment.paymentservice.validator;

import com.ecom.payment.paymentservice.dto.*;
import com.ecom.payment.paymentservice.exception.PaymentException;
import com.ecom.payment.paymentservice.exception.model.ErrorCode;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;


public class PaymentValidator {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RequestValidator.class);
    private static final Map<String, Function<PaymentMethodDetails, Boolean>> VALIDATION_RULES = new HashMap<>();

    static {
        VALIDATION_RULES.put("CREDIT_CARD", details -> {
            CreditCardDTO cc = (CreditCardDTO) details;

            if (!matchesRegex(cc.getCardNumber(), "^[0-9]{16}$")) {
                log.error("Invalid Request - Invalid Credit Card Number: Must be 16 digits");
                throw new PaymentException(ErrorCode.CREDITCARD_NUMBER_INVALID);
            }
            if (!matchesRegex(cc.getExpiry(), "^(0[1-9]|1[0-2])/[0-9]{2}$")) {
                log.error("Invalid Request - Invalid Expiry Date: Must be in MM/YY format");
                throw new PaymentException(ErrorCode.CREDITCARD_EXPIRY_INVALID);
            }
            if (!matchesRegex(cc.getCvv(), "^[0-9]{3}$")) {
                log.error("Invalid Request - Invalid CVV: Must be 3 digits");
                throw new PaymentException(ErrorCode.CREDITCARD_CVV_INVALID);
            }

            return true;
        });

        VALIDATION_RULES.put("DEBIT_CARD", details -> {
            DebitCardDTO dc = (DebitCardDTO) details;

            if (!matchesRegex(dc.getCardNumber(), "^[0-9]{16}$")) {
                log.error("Invalid Request - Invalid Credit Card Number: Must be 16 digits");
                throw new PaymentException(ErrorCode.DEBITCARD_NUMBER_INVALID);
            }
            if (!matchesRegex(dc.getExpiry(), "^(0[1-9]|1[0-2])/[0-9]{2}$")) {
                log.error("Invalid Request - Invalid Expiry Date: Must be in MM/YY format");
                throw new PaymentException(ErrorCode.DEBITCARD_EXPIRY_INVALID);
            }
            if (!matchesRegex(dc.getCvv(), "^[0-9]{3}$")) {
                log.error("Invalid Request - Invalid CVV: Must be 3 digits");
                throw new PaymentException(ErrorCode.DEBITCARD_CVV_INVALID);
            }

            return true;
        });
        VALIDATION_RULES.put("MONEY_TRANSFER", details -> {
            MoneyTransferDTO mt = (MoneyTransferDTO) details;

            if (!matchesRegex(mt.getBankAccountNumber(), "^[0-9]{9,18}$")) {
                log.error("Invalid Request - Invalid Bank Account Number: Must be between 9-18 digits");
                throw new PaymentException(ErrorCode.MONEYTRANSFER_ACCOUNT_INVALID);
            }
            if (!matchesRegex(mt.getIfscCode(), "^[A-Z]{4}[0-9]{7}$")) {
                log.error("Invalid Request - Invalid IFSC Code: Must follow XXXX0000000 format");
                throw new PaymentException(ErrorCode.MONEYTRANSFER_IFSC_INVALID);
            }

            return true;
        });


        VALIDATION_RULES.put("PAYPAL", details -> {
            PaypalDTO pp = (PaypalDTO) details;

            if (!matchesRegex(pp.getPaypalEmail(), "^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$")) {
                log.error("Invalid Request - Invalid PayPal Email: Must be a valid email format");
                throw new PaymentException(ErrorCode.PAYPAL_EMAIL_INVALID);
            }

            return true;
        });

        }

    private static boolean matchesRegex(String value, String regex) {
        return value != null && Pattern.matches(regex, value);
    }

    public static boolean validate(String paymentMethod, PaymentMethodDetails details) {
        if (!VALIDATION_RULES.containsKey(paymentMethod)) {
            log.error("Invalid Request - Unsupported payment method: ");
            throw new PaymentException(ErrorCode.PAYMENT_UNSUPPORTED_METHOD);
        }

        return VALIDATION_RULES.get(paymentMethod).apply(details);
    }

}
