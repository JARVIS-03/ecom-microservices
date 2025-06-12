package com.ecom.payment.paymentservice.service;

import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
import com.ecom.payment.paymentservice.enums.PaymentStatus;
import com.ecom.payment.paymentservice.exception.ResourceNotFoundException;
import com.ecom.payment.paymentservice.model.Payment;
import com.ecom.payment.paymentservice.repository.PaymentRepository;
import com.ecom.payment.paymentservice.retry.RetryLogger;
import com.ecom.payment.paymentservice.utillity.PaymentGatewaySimulator;
import com.ecom.payment.paymentservice.utillity.PaymentMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private OrderServiceClient orderServiceClient;

    @Mock
    private NotificationServiceClient notificationServiceClient;

    @Mock
    private RetryLogger retryLogger;

    @Mock
    private PaymentGatewaySimulator paymentGatewaySimulator;

    private Payment payment;
    private PaymentRequestDTO requestDTO;
    private PaymentResponseDTO responseDTO;

    @BeforeEach
    void setup() {
        requestDTO = new PaymentRequestDTO();
        requestDTO.setOrderId(123L);
        requestDTO.setAmount(100.0);
        requestDTO.setPaymentMethod("CARD");

        payment = new Payment();
        payment.setPaymentId(123L);
        payment.setOrderId(123L);
        payment.setAmount(100.0);
        payment.setStatus(PaymentStatus.INITIATED);
        payment.setPaymentMethod("CARD");

        responseDTO = new PaymentResponseDTO();
        responseDTO.setOrderId(123L);
        responseDTO.setAmount(100.0);
        responseDTO.setStatus(PaymentStatus.SUCCESS.toString());
    }

    @Test
    void testIitiatePayment_Success() throws Exception {
        when(paymentRepository.findById(123L)).thenReturn(Optional.of(payment));
        when(paymentMapper.toEntity(requestDTO)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(responseDTO);
        when(paymentRepository.save(any())).thenReturn(payment);
        when(paymentGatewaySimulator.simulate(anyString())).thenReturn("SUCCESS");

        PaymentResponseDTO paymentResponseDTO = paymentService.initiatePayment(requestDTO);

        assertNotNull(paymentResponseDTO);
        assertEquals(PaymentStatus.SUCCESS.toString(), paymentResponseDTO.getStatus());
        assertEquals(123L, paymentResponseDTO.getOrderId());

        verify(paymentRepository, atLeastOnce()).save(any());
        verify(paymentRepository).findById(123L);
        verify(paymentGatewaySimulator).simulate(anyString());
        verify(notificationServiceClient).sendNotification(paymentResponseDTO, "");
    }


    @Test
    void testInitiatePayment_ResourceNotFoundException() {

        Long invalidPaymentId = 999L;
        when(paymentRepository.findById(invalidPaymentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.updatePaymentStatus(invalidPaymentId, "SUCCESS");
        });

        assertEquals("Payment not found for ID: " + invalidPaymentId, exception.getMessage());
        verify(paymentRepository).findById(invalidPaymentId);
    }



}
