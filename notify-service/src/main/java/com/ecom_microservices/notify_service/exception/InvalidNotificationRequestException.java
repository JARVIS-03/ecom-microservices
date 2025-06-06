package com.ecom_microservices.notify_service.exception;

public class InvalidNotificationRequestException extends RuntimeException {

    public InvalidNotificationRequestException(String message, IllegalArgumentException ex) {
        super(message,ex);
    }
}
