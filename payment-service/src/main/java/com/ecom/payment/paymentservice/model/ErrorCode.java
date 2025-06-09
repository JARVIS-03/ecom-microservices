package com.ecom.payment.paymentservice.model;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // General Payment Errors
    PAYMENT_NOT_FOUND("error.payment.notfound", HttpStatus.NOT_FOUND),
    PAYMENT_VALIDATION_FAILED("error.payment.validation", HttpStatus.BAD_REQUEST),
    PAYMENT_ALREADY_PROCESSED("error.payment.alreadyprocessed", HttpStatus.CONFLICT),
    PAYMENT_GATEWAY_ERROR("error.payment.gateway", HttpStatus.BAD_GATEWAY),
    PAYMENT_TIMEOUT("error.payment.timeout", HttpStatus.REQUEST_TIMEOUT),
    PAYMENT_INTERNAL_ERROR("error.payment.internal", HttpStatus.INTERNAL_SERVER_ERROR),
    PAYMENT_UNAUTHORIZED("error.payment.unauthorized", HttpStatus.UNAUTHORIZED),
    PAYMENT_FORBIDDEN("error.payment.forbidden", HttpStatus.FORBIDDEN),
    PAYMENT_SERVICE_UNAVAILABLE("error.payment.serviceunavailable", HttpStatus.SERVICE_UNAVAILABLE),
    PAYMENT_DUPLICATE_REQUEST("error.payment.duplicate", HttpStatus.CONFLICT),
    PAYMENT_UNSUPPORTED_METHOD("error.payment.unsupportedmethod", HttpStatus.BAD_REQUEST),

    // Credit Card Validation Errors
    CREDITCARD_NUMBER_INVALID("error.payment.creditcard.number.invalid", HttpStatus.BAD_REQUEST),
    CREDITCARD_EXPIRY_INVALID("error.payment.creditcard.expiry.invalid", HttpStatus.BAD_REQUEST),
    CREDITCARD_CVV_INVALID("error.payment.creditcard.cvv.invalid", HttpStatus.BAD_REQUEST),

    // Debit Card Validation Errors
    DEBITCARD_NUMBER_INVALID("error.payment.debitcard.number.invalid", HttpStatus.BAD_REQUEST),
    DEBITCARD_EXPIRY_INVALID("error.payment.debitcard.expiry.invalid", HttpStatus.BAD_REQUEST),
    DEBITCARD_CVV_INVALID("error.payment.debitcard.cvv.invalid", HttpStatus.BAD_REQUEST),

    // Money Transfer Validation Errors
    MONEYTRANSFER_ACCOUNT_INVALID("error.payment.moneytransfer.accountnumber.invalid", HttpStatus.BAD_REQUEST),
    MONEYTRANSFER_IFSC_INVALID("error.payment.moneytransfer.ifsccode.invalid", HttpStatus.BAD_REQUEST),

    // PayPal Validation Errors
    PAYPAL_EMAIL_INVALID("error.payment.paypal.email.invalid", HttpStatus.BAD_REQUEST),

    // Payment Method Unsupported
    PAYMENT_METHOD_UNSUPPORTED("error.payment.method.unsupported", HttpStatus.BAD_REQUEST);

    private final String key;
    private final HttpStatus httpStatus;

    ErrorCode(String key, HttpStatus httpStatus) {
        this.key = key;
        this.httpStatus = httpStatus;
    }

    public String getKey() {
        return key;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
