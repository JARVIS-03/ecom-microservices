package com.ecom.payment.paymentservice.controller;

import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
import com.ecom.payment.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDTO> initiate(@Valid @RequestBody PaymentRequestDTO request) {
        log.info("Initiating payment with payload: {}", request);
        return new ResponseEntity<>(paymentService.initiatePayment(request), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getStatus(@PathVariable Long id) {
        return new ResponseEntity<>(paymentService.getPaymentById(id).getStatus(), HttpStatus.OK);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponseDTO>> getByOrderId(@PathVariable String orderId) {
        return new ResponseEntity<>(paymentService.getPaymentsByOrderId(orderId), HttpStatus.OK);
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentResponseDTO> paymentStatusUpdate(
            @PathVariable Long id,
            @RequestParam String status) {

        log.info("Updating payment status. Payment ID: {}, New Status: {}", id, status);
        PaymentResponseDTO updatedPayment = paymentService.updatePaymentStatus(id, status);
        log.info("Updated payment response: {}", updatedPayment);
        return ResponseEntity.ok(updatedPayment);
    }

}

