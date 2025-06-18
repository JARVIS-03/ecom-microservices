//package com.ecom.payment.paymentservice.service;
//
//import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
//import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
//import com.ecom.payment.paymentservice.enums.PaymentStatus;
//import com.ecom.payment.paymentservice.exception.PaymentException;
//import com.ecom.payment.paymentservice.model.Payment;
//import com.ecom.payment.paymentservice.repository.PaymentRepository;
//import com.ecom.payment.paymentservice.retry.RetryLogger;
//import com.ecom.payment.paymentservice.service.impl.PaymentServiceImpl;
//import com.ecom.payment.paymentservice.utillity.PaymentGatewaySimulator;
//import com.ecom.payment.paymentservice.utillity.PaymentMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.dao.DataAccessException;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class PaymentServiceImplTest {
//
//    @Mock
//    private PaymentRepository paymentRepository;
//
//    @Mock
//    private PaymentMapper paymentMapper;
//
//    @Mock
//    private OrderServiceClient orderServiceClient;
//
//    @Mock
//    private NotificationServiceClient notificationServiceClient;
//
//    @Mock
//    private PaymentGatewaySimulator paymentGatewaySimulator;
//
//    @Mock
//    private RetryLogger retryLogger;
//
//    @InjectMocks
//    private PaymentServiceImpl paymentService;
//
//    private PaymentRequestDTO paymentRequestDTO;
//    private Payment payment;
//    private PaymentResponseDTO paymentResponseDTO;
//
//    @BeforeEach
//    public void setup() {
//
//        paymentRequestDTO = new PaymentRequestDTO();
//        paymentRequestDTO.setOrderId(101L);
//        paymentRequestDTO.setAmount(999.99);
//        paymentRequestDTO.setPaymentMethod("card");
//
//        payment = new Payment();
//        payment.setPaymentId(1L);
//        payment.setOrderId(101L);
//        payment.setStatus(PaymentStatus.INITIATED);
//
//        paymentResponseDTO = new PaymentResponseDTO();
//        paymentResponseDTO.setPaymentId(1L);
//        paymentResponseDTO.setOrderId(101L);
//        paymentResponseDTO.setStatus(PaymentStatus.SUCCESS);
//    }
//
//    @Test
//    public void initiatePayment_shouldInitiatePaymentSuccessfully_whenValidRequestIsGiven() {
//
//        when(paymentRepository.findByOrderId(101L)).thenReturn(Collections.emptyList());
//        when(paymentMapper.toEntity(paymentRequestDTO)).thenReturn(payment);
//        when(paymentRepository.save(payment)).thenReturn(payment);
//        when(paymentGatewaySimulator.simulate("card")).thenReturn("SUCCESS");
//        when(paymentRepository.findById(1L)).thenReturn(java.util.Optional.of(payment));
//        when(paymentRepository.save(payment)).thenReturn(payment);
//        when(paymentMapper.toDto(payment)).thenReturn(paymentResponseDTO);
//
//        PaymentResponseDTO response = paymentService.initiatePayment(paymentRequestDTO);
//
//        assertEquals(101L, response.getOrderId());
//        assertEquals(PaymentStatus.SUCCESS.toString(), response.getStatus());
//
//        verify(notificationServiceClient, times(1)).sendNotification(paymentResponseDTO);
//        verify(paymentRepository, times(2)).save(payment);
//    }
//
//    @Test
//    public void initiatePayment_shouldThrowException_whenPaymentAlreadyCompleted() {
//
//        Payment existingSuccessfulPayment = new Payment();
//        existingSuccessfulPayment.setOrderId(101L);
//        existingSuccessfulPayment.setStatus(PaymentStatus.SUCCESS);
//
//        when(paymentRepository.findByOrderId(101L))
//                .thenReturn(Collections.singletonList(existingSuccessfulPayment));
//
//
//        IllegalStateException exception = org.junit.jupiter.api.Assertions.assertThrows(
//                IllegalStateException.class,
//                () -> paymentService.initiatePayment(paymentRequestDTO)
//        );
//
//        assertEquals("Payment already completed for order ID: 101", exception.getMessage());
//
//        verify(paymentRepository, times(1)).findByOrderId(101L);
//        verifyNoInteractions(paymentMapper, paymentGatewaySimulator, notificationServiceClient);
//    }
//
//    @Test
//    public void initiatePayment_shouldThrowException_whenInvalidStatusReturnedFromGateway() {
//
//        when(paymentRepository.findByOrderId(101L)).thenReturn(Collections.emptyList());
//        when(paymentMapper.toEntity(paymentRequestDTO)).thenReturn(payment);
//        when(paymentRepository.save(payment)).thenReturn(payment);
//        when(paymentGatewaySimulator.simulate("card")).thenReturn("INVALID_STATUS");
//
//        when(paymentRepository.findById(1L)).thenReturn(java.util.Optional.of(payment));
//
//        IllegalStateException exception = org.junit.jupiter.api.Assertions.assertThrows(
//                IllegalStateException.class,
//                () -> paymentService.initiatePayment(paymentRequestDTO)
//        );
//
//        assertEquals("Invalid Payment Status: INVALID_STATUS", exception.getMessage());
//
//        verify(paymentRepository, times(1)).save(payment);
//        verifyNoInteractions(notificationServiceClient);
//    }
//
//    @Test
//    public void savePaymentWithRetry_shouldSavePaymentSuccessfully_whenNoExceptionThrown() {
//
//        when(paymentRepository.save(payment)).thenReturn(payment);
//
//
//        Payment saved = paymentService.savePaymentWithRetry(payment);
//
//
//        assertEquals(payment, saved);
//        verify(paymentRepository, times(1)).save(payment);
//    }
//
//    @Test
//    public void recoverSavePayment_shouldThrowException_afterRetriesFail() {
//
//        DataAccessException ex = new DataAccessException("DB write failed") {};
//
//        IllegalStateException exception = org.junit.jupiter.api.Assertions.assertThrows(
//                IllegalStateException.class,
//                () -> paymentService.recoverSavePayment(ex, payment)
//        );
//
//        assertEquals("Could not save payment after retries", exception.getMessage());
//    }
//
//    @Test
//    public void updatePaymentStatus_shouldUpdateSuccessfully_whenValidStatusIsGiven() {
//
//        payment.setStatus(PaymentStatus.INITIATED);
//
//        when(paymentRepository.findById(1L)).thenReturn(java.util.Optional.of(payment));
//        when(paymentRepository.save(payment)).thenReturn(payment);
//        when(paymentMapper.toDto(payment)).thenReturn(paymentResponseDTO);
//
//        PaymentResponseDTO response = paymentService.updatePaymentStatus(1L, "SUCCESS");
//
//        assertEquals(PaymentStatus.SUCCESS.toString(), response.getStatus());
//        verify(paymentRepository, times(1)).findById(1L);
//        verify(paymentRepository, times(1)).save(payment);
//        verify(paymentMapper, times(1)).toDto(payment);
//    }
//
//    @Test
//    public void updatePaymentStatus_shouldThrowException_whenPaymentNotFound() {
//
//        when(paymentRepository.findById(1L)).thenReturn(java.util.Optional.empty());
//
//        PaymentException exception = org.junit.jupiter.api.Assertions.assertThrows(
//                PaymentException.class,
//                () -> paymentService.updatePaymentStatus(1L, "SUCCESS")
//        );
//
//        assertEquals("Payment not found for ID: 1", exception.getMessage());
//
//        verify(paymentRepository, times(1)).findById(1L);
//        verifyNoMoreInteractions(paymentRepository);
//        verifyNoInteractions(paymentMapper);
//    }
//
//    @Test
//    public void updatePaymentStatus_shouldThrowException_whenStatusIsInvalid() {
//
//        when(paymentRepository.findById(1L)).thenReturn(java.util.Optional.of(payment));
//
//
//        IllegalStateException exception = org.junit.jupiter.api.Assertions.assertThrows(
//                IllegalStateException.class,
//                () -> paymentService.updatePaymentStatus(1L, "PAID_SUCCESSFULLY")
//        );
//
//        assertEquals("Invalid Payment Status: PAID_SUCCESSFULLY", exception.getMessage());
//
//        verify(paymentRepository, times(1)).findById(1L);
//        verifyNoMoreInteractions(paymentRepository);
//        verifyNoInteractions(paymentMapper);
//    }
//
//    @Test
//    public void getPaymentById_shouldReturnPayment_whenPaymentExists() {
//
//        when(paymentRepository.findById(1L)).thenReturn(java.util.Optional.of(payment));
//        when(paymentMapper.toDto(payment)).thenReturn(paymentResponseDTO);
//
//
//        PaymentResponseDTO response = paymentService.getPaymentById(1L);
//
//
//        assertEquals(101L, response.getOrderId());
//        assertEquals(paymentResponseDTO, response);
//        verify(paymentRepository, times(1)).findById(1L);
//        verify(paymentMapper, times(1)).toDto(payment);
//    }
//
//    @Test
//    public void getPaymentById_shouldThrowException_whenPaymentNotFound() {
//
//        when(paymentRepository.findById(1L)).thenReturn(java.util.Optional.empty());
//
//
//        PaymentException exception = org.junit.jupiter.api.Assertions.assertThrows(
//                PaymentException.class,
//                () -> paymentService.getPaymentById(1L)
//        );
//
//        assertEquals("Payment not found for ID: 1", exception.getMessage());
//
//        verify(paymentRepository, times(1)).findById(1L);
//        verifyNoInteractions(paymentMapper);
//    }
//
//    @Test
//    public void getPaymentsByOrderId_shouldReturnList_whenPaymentsExist() {
//
//        Payment payment1 = new Payment();
//        payment1.setPaymentId(1L);
//        payment1.setOrderId(101L);
//        payment1.setStatus(PaymentStatus.SUCCESS);
//
//        Payment payment2 = new Payment();
//        payment2.setPaymentId(2L);
//        payment2.setOrderId(101L);
//        payment2.setStatus(PaymentStatus.REFUNDED);
//
//        PaymentResponseDTO dto1 = new PaymentResponseDTO();
//        dto1.setPaymentId(1L);
//        dto1.setOrderId(101L);
//        dto1.setStatus(PaymentStatus.SUCCESS);
//
//        PaymentResponseDTO dto2 = new PaymentResponseDTO();
//        dto2.setPaymentId(2L);
//        dto2.setOrderId(101L);
//        dto2.setStatus(PaymentStatus.REFUNDED);
//
//        when(paymentRepository.findByOrderId(101L)).thenReturn(List.of(payment1, payment2));
//        when(paymentMapper.toDto(payment1)).thenReturn(dto1);
//        when(paymentMapper.toDto(payment2)).thenReturn(dto2);
//
//        List<PaymentResponseDTO> result = paymentService.getPaymentsByOrderId(101L);
//
//        assertEquals(2, result.size());
//        assertEquals(dto1, result.get(0));
//        assertEquals(dto2, result.get(1));
//
//        verify(paymentRepository, times(1)).findByOrderId(101L);
//        verify(paymentMapper, times(1)).toDto(payment1);
//        verify(paymentMapper, times(1)).toDto(payment2);
//    }
//
//    @Test
//    public void refundPayment_shouldRefundSuccessfully_whenValidSuccessfulPaymentExists() {
//
//        Payment successfulPayment = new Payment();
//        successfulPayment.setPaymentId(1L);
//        successfulPayment.setOrderId(101L);
//        successfulPayment.setStatus(PaymentStatus.SUCCESS);
//
//        Payment refundedPayment = new Payment();
//        refundedPayment.setPaymentId(1L);
//        refundedPayment.setOrderId(101L);
//        refundedPayment.setStatus(PaymentStatus.REFUNDED);
//
//        PaymentResponseDTO refundedDto = new PaymentResponseDTO();
//        refundedDto.setPaymentId(1L);
//        refundedDto.setOrderId(101L);
//        refundedDto.setStatus(PaymentStatus.REFUNDED);
//
//        when(paymentRepository.findByOrderId(101L)).thenReturn(List.of(successfulPayment));
//        when(paymentRepository.save(successfulPayment)).thenReturn(refundedPayment);
//        when(paymentMapper.toDto(refundedPayment)).thenReturn(refundedDto);
//
//        PaymentResponseDTO response = paymentService.refundPayment(101L);
//
//        assertEquals(PaymentStatus.REFUNDED.toString(), response.getStatus());
//        assertEquals(101L, response.getOrderId());
//
//        verify(orderServiceClient, times(1)).validateOrder(101L);
//        verify(orderServiceClient, times(1)).updateOrderStatus(101L, "REFUNDED");
//        verify(notificationServiceClient, times(1)).sendNotification(refundedDto);
//        verify(paymentRepository, times(1)).save(successfulPayment);
//    }
//
//    @Test
//    public void refundPayment_shouldThrowException_whenNoPaymentsFound() {
//
//        when(paymentRepository.findByOrderId(101L)).thenReturn(Collections.emptyList());
//
//        PaymentException exception = org.junit.jupiter.api.Assertions.assertThrows(
//                PaymentException.class,
//                () -> paymentService.refundPayment(101L)
//        );
//
//        assertEquals("No payments found for Order ID: 101", exception.getMessage());
//
//        verify(orderServiceClient, times(1)).validateOrder(101L);
//        verify(paymentRepository, times(1)).findByOrderId(101L);
//        verifyNoMoreInteractions(paymentRepository);
//        verifyNoInteractions(notificationServiceClient);
//        verifyNoMoreInteractions(orderServiceClient);
//
//    }
//
//    @Test
//    public void refundPayment_shouldThrowException_whenNoSuccessfulPaymentFound() {
//
//        Payment failedPayment = new Payment();
//        failedPayment.setPaymentId(1L);
//        failedPayment.setOrderId(101L);
//        failedPayment.setStatus(PaymentStatus.FAILED);
//
//        Payment pendingPayment = new Payment();
//        pendingPayment.setPaymentId(2L);
//        pendingPayment.setOrderId(101L);
//        pendingPayment.setStatus(PaymentStatus.INITIATED);
//
//        when(paymentRepository.findByOrderId(101L)).thenReturn(List.of(failedPayment, pendingPayment));
//
//        PaymentException exception = org.junit.jupiter.api.Assertions.assertThrows(
//                PaymentException.class,
//                () -> paymentService.refundPayment(101L)
//        );
//
//        assertEquals("No successful payment found to refund for Order ID: 101", exception.getMessage());
//
//        verify(orderServiceClient, times(1)).validateOrder(101L);
//        verify(paymentRepository, times(1)).findByOrderId(101L);
//        verifyNoMoreInteractions(paymentRepository);
//        verifyNoInteractions(notificationServiceClient);
//        verify(orderServiceClient, never()).updateOrderStatus(anyLong(), anyString());
//    }
//
//
//}