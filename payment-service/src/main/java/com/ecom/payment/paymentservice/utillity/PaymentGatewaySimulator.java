package com.ecom.payment.paymentservice.utillity;

import com.ecom.payment.paymentservice.exception.PaymentException;
import com.ecom.payment.paymentservice.model.ErrorCode;
import com.ecom.payment.paymentservice.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class PaymentGatewaySimulator {
    private static final Logger log = LoggerFactory.getLogger(PaymentGatewaySimulator.class);
    @Retryable(
            retryFor = RuntimeException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000),
            listeners = "retryLogger"
    )
    public String simulate(String method) {
        log.info("Simulating payment for method: {}", method);
        String status = Math.random() > 0.2 ? "SUCCESS" : "FAILED";
        if("FAILED".equals(status)) {
            log.warn("Simulating payment failed for method: {}", method);
            throw new PaymentException(ErrorCode.PAYMENT_GATEWAY_ERROR);
        }
        log.debug("Mock gateway response for {}: {}", method, status);
        return status;
    }

    @Recover
    public String recoverSimulation(RuntimeException ex, String method) {
        log.error("Payment gateway failed after retries for method: {}", method, ex);
        throw new PaymentException(ErrorCode.PAYMENT_GATEWAY_ERROR);
    }
}
