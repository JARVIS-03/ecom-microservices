package com.ecom_microservices.notify_service.exception;

public class NotificationProcessingException extends RuntimeException {

    public NotificationProcessingException(String format) {
       super(format);
    }

    public NotificationProcessingException(String format, Exception e) {
      super(format,e);
    }
}
