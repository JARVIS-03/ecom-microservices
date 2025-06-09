package com.ecom.payment.paymentservice.controller;

import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
import com.ecom.payment.paymentservice.enums.PaymentStatus;
import com.ecom.payment.paymentservice.service.PaymentService;
import com.ecom.payment.paymentservice.validator.RequestValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;


    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDTO> initiate(@Valid @RequestBody PaymentRequestDTO request) {
            RequestValidator.validatePaymentDetails(request);
            log.info("Initiating payment with payload: {}", request);
            return ResponseEntity.ok(paymentService.initiatePayment(request));
    }


    @GetMapping("/{id}")
    public ResponseEntity<String> getPaymentStatus(@PathVariable Long id) {
        log.info("Fetching payment status for ID: {}", id);

        PaymentStatus status = paymentService.getPaymentById(id).getStatus();
        log.info("Payment status fetched: ID = {}, status = {}", id, status);

        return ResponseEntity.ok(status.toString());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponseDTO>> getByOrderId(@PathVariable Long orderId) {
        return new ResponseEntity<>(paymentService.getPaymentsByOrderId(orderId), HttpStatus.OK);
    }

    @PutMapping("/{id}/{status}")
    public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(
            @PathVariable Long id,
            @PathVariable String status) {

        log.info("Updating payment status. Payment ID: {}, New Status: {}", id, status);

        PaymentResponseDTO updatedPayment = paymentService.updatePaymentStatus(id, status);
        log.info("Payment status updated successfully: Payment ID = {}, Update Status = {}", updatedPayment.getPaymentId(), updatedPayment.getStatus());

        return ResponseEntity.ok(updatedPayment);
    }

    @PutMapping("/refund/{orderId}")
    public ResponseEntity<PaymentResponseDTO> refundPayment(@PathVariable Long orderId) {
        log.info("Initiating refund for orderId: {}", orderId);

        PaymentResponseDTO refundedPayment = paymentService.refundPayment(orderId);
        log.info("Refund processed successfully: paymentId = {}, status = {}", refundedPayment.getPaymentId(), refundedPayment.getStatus());

        return ResponseEntity.ok(refundedPayment);
    }

}

