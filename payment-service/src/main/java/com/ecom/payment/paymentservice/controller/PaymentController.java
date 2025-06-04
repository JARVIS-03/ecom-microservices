package com.ecom.payment.paymentservice.controller;

import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
import com.ecom.payment.paymentservice.service.PaymentService;
import com.ecom.payment.paymentservice.validator.RequestValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;


    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDTO> initiate(@Valid @RequestBody PaymentRequestDTO request) {
        RequestValidator.validatePaymentDetails(request);
        log.info("Initiating payment with payload: {}", request);
        return new ResponseEntity<>(paymentService.initiatePayment(request), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getStatus(@PathVariable Long id) {
        log.info("Fetching payment status for ID: {}", id);
        String status = paymentService.getPaymentById(id).getStatus();
        log.info("Payment status for ID {}: {}", id, status);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }



    @PutMapping("/refund/{orderId}")
    public ResponseEntity<PaymentResponseDTO> refundPayment(@PathVariable String orderId) {
        PaymentResponseDTO refundedPayment = paymentService.refundPayment(orderId);
        return ResponseEntity.ok(refundedPayment);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponseDTO>> getByOrderId(@PathVariable String orderId) {
        log.info("Fetching all payments for Order ID: {}", orderId);
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByOrderId(orderId);
        log.info("Total {} payments found for Order ID: {}", payments.size(), orderId);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }
    @PutMapping("/{id}/{status}")
    public ResponseEntity<PaymentResponseDTO> paymentStatusUpdate(
            @PathVariable Long id,
            @PathVariable String status) {

        log.info("Updating payment status. Payment ID: {}, New Status: {}", id, status);
        PaymentResponseDTO updatedPayment = paymentService.updatePaymentStatus(id, status);
        log.info("Payment status updated. Updated payment: {}", updatedPayment);
        return ResponseEntity.ok(updatedPayment);
    }

}

