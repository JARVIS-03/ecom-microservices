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
        return new ResponseEntity<>(paymentService.getPaymentById(id).getStatus(), HttpStatus.OK);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponseDTO>> getByOrderId(@PathVariable String orderId) {
        RequestValidator.validateRequestParam(orderId);
        return new ResponseEntity<>(paymentService.getPaymentsByOrderId(orderId), HttpStatus.OK);
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

