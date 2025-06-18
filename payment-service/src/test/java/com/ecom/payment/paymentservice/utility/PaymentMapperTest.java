//package com.ecom.payment.paymentservice.utility;
//
//import com.ecom.payment.paymentservice.dto.PaymentMethodDetails;
//import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
//import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
//import com.ecom.payment.paymentservice.enums.PaymentStatus;
//import com.ecom.payment.paymentservice.model.Payment;
//import com.ecom.payment.paymentservice.utillity.PaymentMapper;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.modelmapper.ModelMapper;
//
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class PaymentMapperTest {
//
//    @Mock
//    private ModelMapper modelMapper;
//
//    @InjectMocks
//    private PaymentMapper paymentMapper;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    private PaymentRequestDTO requestDTO;
//    private Payment payment;
//
//    @BeforeEach
//    void setUp() {
//        requestDTO = new PaymentRequestDTO();
//        //
//
//        payment = new Payment();
//        payment.setPaymentId(1L);
//        payment.setStatus(PaymentStatus.INITIATED);
//        payment.setDate(LocalDateTime.now());
//        payment.setPaymentDetails("{}"); // Sample serialized JSON
//
//        when(modelMapper.map(requestDTO, Payment.class)).thenReturn(payment);
//        when(modelMapper.map(payment, PaymentResponseDTO.class)).thenReturn(new PaymentResponseDTO());
//    }
//
//    @Test
//    void testToEntity() {
//        Payment mappedPayment = paymentMapper.toEntity(requestDTO);
//
//        assertNotNull(mappedPayment);
//        assertEquals(PaymentStatus.INITIATED, mappedPayment.getStatus());
//        assertNotNull(mappedPayment.getDate());
//        assertEquals("{}", mappedPayment.getPaymentDetails()); // Validating serialization
//
//        verify(modelMapper).map(requestDTO, Payment.class);
//    }
//
//    @Test
//    void testToDto() {
//        PaymentResponseDTO responseDTO = paymentMapper.toDto(payment);
//
//        assertNotNull(responseDTO);
//        assertNotNull(responseDTO.getMethodDetails());
//
//        verify(modelMapper).map(payment, PaymentResponseDTO.class);
//    }
//
//    @Test
//    void testSerializationFailure() {
//        assertThrows(RuntimeException.class, () -> paymentMapper.serializeMethodDetails(null));
//    }
//
//    @Test
//    void testDeserializationFailure() {
//        payment.setPaymentDetails("invalid-json");
//        assertThrows(RuntimeException.class, () -> paymentMapper.toDto(payment));
//    }
//}