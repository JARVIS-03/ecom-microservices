package com.ecom.payment.paymentservice.validator;

import com.ecom.payment.paymentservice.dto.*;
import com.ecom.payment.paymentservice.exception.PaymentProcessingException;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
public class RequestValidator {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RequestValidator.class);
     public static void validateRequestParam(String value) {

         if(StringUtils.isBlank(value)){
             log.error("Invalid Request Param : {}",value);
             throw new PaymentProcessingException("Invalid Request");
         }
     }
    public static void validateRequestParam(String value, int maxAllowedLimit) {
         validateRequestParam(value);
        if(value.length() > maxAllowedLimit){
            log.error("Invalid Request Param : {}",value);
            throw new PaymentProcessingException("Invalid Request");
        }
    }


    public static void validatePaymentDetails(PaymentRequestDTO request){
         if(request == null){
             log.error("Invalid Request - request is empty");
             throw new PaymentProcessingException("Invalid Request");
         }
        if (!PaymentValidator.validate(request.getPaymentMethod(), request.getMethodDetails())) {
            log.error("Invalid Request - Invalid payment method details provided");
            throw new PaymentProcessingException("Invalid payment method details provided");
        }

    }
}
