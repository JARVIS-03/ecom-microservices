package com.ecom_microservices.notify_service.exception;

public class NotificationNotFoundException extends RuntimeException {

    public NotificationNotFoundException(String message) {
        super(message);
    }
}
