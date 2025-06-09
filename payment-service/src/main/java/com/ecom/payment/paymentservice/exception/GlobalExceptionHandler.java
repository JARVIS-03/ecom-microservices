package com.ecom.payment.paymentservice.exception;

import com.ecom.payment.paymentservice.exception.model.ErrorCode;
import com.ecom.payment.paymentservice.exception.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.ServiceUnavailableException;
import java.util.Locale;



@RestControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private MessageSource messageSource;


    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    private ErrorResponse buildErrorResponse(ErrorCode errorCode, Locale locale) {
        // Fetch localized error message from properties
        String message = messageSource.getMessage(errorCode.getKey() + ".message", null, locale);

        // Fetch custom error code as string and parse to int
        String codeString = messageSource.getMessage(errorCode.getKey() + ".code", null, locale);
        int customCode = Integer.parseInt(codeString);

        // Get HTTP status code as int
        int httpStatusCode = errorCode.getHttpStatus().value();

        // Build and return ErrorResponse DTO
        return new ErrorResponse(httpStatusCode, customCode, message);
    }


    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException ex, Locale locale) {
        log.error(" PaymentException: {}", ex);
        ErrorResponse errorResponse = buildErrorResponse(ex.getErrorCode(), locale);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getHttpStatus()));
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailable(ServiceUnavailableException ex, Locale locale) {
        
        log.error(" ServiceUnavailableException: {}, URI: {}", ex.getMessage(), ex);

        ErrorResponse response = buildErrorResponse(ErrorCode.PAYMENT_SERVICE_UNAVAILABLE, locale);
        return new ResponseEntity<>(response, ErrorCode.PAYMENT_SERVICE_UNAVAILABLE.getHttpStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, Locale locale) {
        
        log.error(" ConstraintViolationException: {}, URI: {}", ex.getMessage(),  ex);

        ErrorResponse response = buildErrorResponse(ErrorCode.PAYMENT_VALIDATION_FAILED, locale);
        return new ResponseEntity<>(response, ErrorCode.PAYMENT_VALIDATION_FAILED.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, Locale locale) {
        
        log.error(" MethodArgumentNotValidException: {}, URI: {}", ex.getMessage(),  ex);

        ErrorResponse response = buildErrorResponse(ErrorCode.PAYMENT_VALIDATION_FAILED, locale);
        return new ResponseEntity<>(response, ErrorCode.PAYMENT_VALIDATION_FAILED.getHttpStatus());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, Locale locale) {
        
        log.error(" MissingServletRequestParameterException: {}, URI: {}", ex.getMessage(),  ex);

        ErrorResponse response = buildErrorResponse(ErrorCode.PAYMENT_VALIDATION_FAILED, locale);
        return new ResponseEntity<>(response, ErrorCode.PAYMENT_VALIDATION_FAILED.getHttpStatus());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, Locale locale) {
        
        log.error(" HttpRequestMethodNotSupportedException: {}, URI: {}", ex.getMessage(),  ex);

        ErrorResponse response = buildErrorResponse(ErrorCode.PAYMENT_METHOD_UNSUPPORTED, locale);
        return new ResponseEntity<>(response, ErrorCode.PAYMENT_METHOD_UNSUPPORTED.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex, Locale locale) {
        
        log.error(" Uncaught Exception: {}, URI: {}", ex.getMessage(),  ex);

        ex.printStackTrace(); // To verify what is being thrown
        ErrorResponse response = new ErrorResponse(500, 9999, "Unhandled exception: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
