package com.ecom.payment.paymentservice.service;

import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
import com.ecom.payment.paymentservice.enums.PaymentStatus;
import com.ecom.payment.paymentservice.model.Payment;
import com.ecom.payment.paymentservice.repository.PaymentRepository;
import com.ecom.payment.paymentservice.retry.RetryLogger;
import com.ecom.payment.paymentservice.utillity.PaymentConverter;
import com.ecom.payment.paymentservice.utillity.PaymentGatewaySimulator;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentConverter paymentConverter;

    @Mock
    private ObjectMapper objectMapper;

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
    void testIitiatePayment_Success() throws Exception{
        when(paymentRepository.findByOrderId(123L)).thenReturn(List.of());
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(paymentRepository.save(any())).thenReturn(payment);
        when(paymentGatewaySimulator.simulate(anyString())).thenReturn("SUCCESS");
        when(paymentConverter.toDTO(any())).thenReturn(responseDTO);

        PaymentResponseDTO paymentResponseDTO = paymentService.initiatePayment(requestDTO);

        assertNotNull(paymentResponseDTO);
        assertEquals(PaymentStatus.SUCCESS.toString(), paymentResponseDTO.getStatus());
        assertEquals(123L,  paymentResponseDTO.getOrderId());

        verify(paymentRepository).findByOrderId(123L);
        verify(paymentRepository).save(any());
        verify(paymentGatewaySimulator).simulate(anyString());
        verify(notificationServiceClient).sendNotification(paymentResponseDTO, "");
    }

    @Test
    void testInitiatePayment_IllegalStateException() {
        Payment successPayment = new Payment();
        successPayment.setStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByOrderId(123L)).thenReturn(List.of(successPayment));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.initiatePayment(requestDTO);
        });

        assertEquals("Payment already completed for order ID: 123", exception.getMessage());
        verify(paymentRepository).findByOrderId(123L);
    }

    @Test
    void testInitiatePayment_JsonProcessingException() throws JsonProcessingException {
        when(paymentRepository.findByOrderId(123L)).thenReturn(List.of());
        when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.initiatePayment(requestDTO);
        });

        assertEquals("Failed to serialize payment method details",  exception.getMessage());
        verify(objectMapper).writeValueAsString(any());
    }

}
