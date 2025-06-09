package com.ecom.payment.paymentservice.exception;

import com.ecom.payment.paymentservice.model.ErrorCode;

public class PaymentException extends RuntimeException {
    private final ErrorCode errorCode;

    public PaymentException(ErrorCode errorCode) {
        super(errorCode.getKey());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
