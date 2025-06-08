//package com.ecom.payment.paymentservice.controller;
//
//import com.ecom.payment.paymentservice.dto.PaymentRequestDTO;
//import com.ecom.payment.paymentservice.dto.PaymentResponseDTO;
//import com.ecom.payment.paymentservice.service.PaymentService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//class PaymentControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private PaymentService paymentService;
//
//    @InjectMocks
//    private PaymentController paymentController;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
//    }
//
//    @Test
//    void testInitiatePayment() throws Exception {
//        PaymentRequestDTO request = new PaymentRequestDTO(); // Fill fields if needed
//        PaymentResponseDTO response = createDummyResponse();
//
//        when(paymentService.initiatePayment(any())).thenReturn(response);
//
//        mockMvc.perform(post("/payments/initiate")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.paymentId").value(response.getPaymentId()))
//                .andExpect(jsonPath("$.status").value(response.getStatus()));
//    }
//
//    @Test
//    void testGetPaymentStatus() throws Exception {
//        PaymentResponseDTO response = createDummyResponse();
//        when(paymentService.getPaymentById(1L)).thenReturn(response);
//
//        mockMvc.perform(get("/payments/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(response.getStatus()));
//    }
//
//    @Test
//    void testRefundPayment() throws Exception {
//        PaymentResponseDTO response = createDummyResponse();
//        response.setStatus("REFUNDED");
//
//        when(paymentService.refundPayment("ORD001")).thenReturn(response);
//
//        mockMvc.perform(put("/payments/refund/ORD001"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.orderId").value("ORD001"))
//                .andExpect(jsonPath("$.status").value("REFUNDED"));
//    }
//
//    @Test
//    void testGetByOrderId() throws Exception {
//        PaymentResponseDTO response = createDummyResponse();
//        response.setPaymentId(1L);
//
//        when(paymentService.getPaymentsByOrderId("ORD001")).thenReturn(List.of(response));
//
//        mockMvc.perform(get("/payments/order/ORD001"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(1))
//                .andExpect(jsonPath("$[0].orderId").value("ORD001"));
//    }
//
//    @Test
//    void testUpdatePaymentStatus() throws Exception {
//        PaymentResponseDTO response = createDummyResponse();
//        response.setStatus("FAILED");
//
//        when(paymentService.updatePaymentStatus(1L, "FAILED")).thenReturn(response);
//
//        mockMvc.perform(put("/payments/1/FAILED"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("FAILED"));
//    }
//
//    private PaymentResponseDTO createDummyResponse() {
//        PaymentResponseDTO dto = new PaymentResponseDTO();
//        dto.setPaymentId(1L);
//        dto.setOrderId("ORD001");
//        dto.setAmount(100.0);
//        dto.setStatus("SUCCESS");
//        dto.setPaymentMethod("DEBIT_CARD");
//        dto.setDate(LocalDateTime.now());
//        return dto;
//    }
//}
