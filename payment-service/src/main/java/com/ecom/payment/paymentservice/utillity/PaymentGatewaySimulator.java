package com.ecom.payment.paymentservice.utillity;

import com.ecom.payment.paymentservice.controller.PaymentController;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Slf4j
public class PaymentGatewaySimulator {
    private static final Logger log = LoggerFactory.getLogger(PaymentGatewaySimulator.class);
    public static String simulate(String method) {
        String status = Math.random() > 0.2 ? "SUCCESS" : "FAILED";
        log.debug("Mock gateway response for {}: {}", method, status);
        return status;
    }
}
