package com.ecom.payment.paymentservice.controller;

import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
import com.ecom.payment.paymentservice.service.PaymentService;
import com.ecom.payment.paymentservice.service.PaymentServiceImpl;
import com.ecom.payment.paymentservice.validator.RequestValidator;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
//@Slf4j
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;


    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponseDTO> initiatePayment( @RequestBody PaymentRequestDTO request) {
        log.info("Received payment initiation request: orderId = {}, method = {}", request.getOrderId(), request.getPaymentMethod());
        RequestValidator.validatePaymentDetails(request);

        PaymentResponseDTO response = paymentService.initiatePayment(request);
        log.info("Payment initiated successfully: paymentId = {}, status = {}", response.getPaymentId(), response.getStatus());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getPaymentStatus(@PathVariable Long id) {
        log.info("Fetching payment status for ID: {}", id);

        String status = paymentService.getPaymentById(id).getStatus();
        log.info("Payment status fetched: ID = {}, status = {}", id, status);

        return ResponseEntity.ok(status);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByOrderId(@PathVariable Long orderId) {
        log.info("Fetching payments for order ID: {}", orderId);
//        RequestValidator.validateRequestParam(orderId);

        List<PaymentResponseDTO> listOfAllPayments = paymentService.getPaymentsByOrderId(orderId);
        log.info("Payments fetched for order ID {}: count = {}", orderId, listOfAllPayments.size());

        return ResponseEntity.ok(listOfAllPayments);
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

