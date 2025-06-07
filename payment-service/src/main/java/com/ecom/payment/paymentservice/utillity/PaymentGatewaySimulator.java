package com.ecom.payment.paymentservice.utillity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentGatewaySimulator {
    public static String simulate(String method) {
        String status = Math.random() > 0.2 ? "SUCCESS" : "FAILED";
        log.debug("Mock gateway response for {}: {}", method, status);
        return status;
    }
}
