package com.ecom.payment.paymentservice.validator;

import com.ecom.payment.paymentservice.dto.*;
import com.ecom.payment.paymentservice.exception.PaymentException;
import com.ecom.payment.paymentservice.model.ErrorCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
public class RequestValidator {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RequestValidator.class);
     public static void validateRequestParam(String value) {

         if(StringUtils.isBlank(value)){
             log.error("Invalid Request Param : {}",value);
             throw new PaymentException(ErrorCode.PAYMENT_VALIDATION_FAILED);
         }
     }
    public static void validateRequestParam(String value, int maxAllowedLimit) {
         validateRequestParam(value);
        if(value.length() > maxAllowedLimit){
            log.error("Invalid Request Param : {}",value);
            throw new PaymentException(ErrorCode.PAYMENT_VALIDATION_FAILED);
        }
    }


    public static void validatePaymentDetails(PaymentRequestDTO request){
         if(request == null){
             log.error("Invalid Request - request is empty");
             throw new PaymentException(ErrorCode.PAYMENT_VALIDATION_FAILED);
         }
        if (!PaymentValidator.validate(request.getPaymentMethod(), request.getMethodDetails())) {
            log.error("Invalid Request - Invalid payment method details provided");
            throw new PaymentException(ErrorCode.PAYMENT_VALIDATION_FAILED);
        }

    }
}
