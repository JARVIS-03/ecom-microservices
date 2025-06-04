package com.ecom_microservices.notify_service.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<String> handleNotificationNotFound(NotificationNotFoundException ex) {
        logger.error("Notification not found error: ", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidNotificationRequestException.class)
    public ResponseEntity<String> handleInvalidNotificationRequest(InvalidNotificationRequestException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllUnhandledExceptions(Exception ex) {
        return ResponseEntity.internalServerError()
                .body("An unexpected error occurred: " + ex.getMessage());
    }
    
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String errorMessage = "HTTP method " + ex.getMethod() + " not supported for this endpoint.";
        logger.info("HTTP method " + ex.getMethod() + " not supported for this endpoint.");
        logger.warn(errorMessage);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorMessage);
    }
    
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException(DataAccessException ex) {
        logger.error("Database access error: ", ex);
        String message = "A database error occurred. Please try again later.";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }
    
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, IllegalArgumentException.class})
    public ResponseEntity<String> handleInputMismatchExceptions(Exception ex) {
        logger.error("Invalid request by user: ", ex);
        String message = "You have sent an invalid request. ";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        logger.error("Validation failed for request: ", ex);
        StringBuilder messageBuilder = new StringBuilder("Invalid request. Issues:\n");
        ex.getConstraintViolations().forEach(violation -> {
            messageBuilder.append("- ")
                          .append(violation.getPropertyPath())
                          .append(": ")
                          .append(violation.getMessage())
                          .append("\n");
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageBuilder.toString());
    }
}
