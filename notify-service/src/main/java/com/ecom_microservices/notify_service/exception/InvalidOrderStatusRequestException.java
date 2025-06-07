package com.ecom_microservices.notify_service.exception;

public class InvalidOrderStatusRequestException extends RuntimeException {
    public InvalidOrderStatusRequestException(String message) {
        super(message);
    }

    public InvalidOrderStatusRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
