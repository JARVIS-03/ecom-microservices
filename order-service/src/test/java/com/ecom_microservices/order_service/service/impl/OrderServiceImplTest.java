package com.ecom_microservices.order_service.service.impl;

import com.ecom_microservices.order_service.dto.request.OrderRequest;
import com.ecom_microservices.order_service.dto.response.OrderResponse;
import com.ecom_microservices.order_service.dto.response.ProductResponse;
import com.ecom_microservices.order_service.entity.Order;
import com.ecom_microservices.order_service.entity.OrderItem;
import com.ecom_microservices.order_service.enums.OrderStatus;
import com.ecom_microservices.order_service.exception.InvalidOrderStatusException;
import com.ecom_microservices.order_service.exception.ResourceNotFoundException;
import com.ecom_microservices.order_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequest orderRequest;
    private Order order;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        OrderItem item = new OrderItem();
        item.setProductId("P001");
        item.setProductPrice(100);
        item.setQuantity(2);

        orderRequest = new OrderRequest();
        orderRequest.setCustomerIdentifier(123L);
        orderRequest.setOrderItems(List.of(item));

        order = new Order();
        order.setId(1L);
        order.setCustomerIdentifier(123L);
        order.setOrderItems(List.of(item));
        order.setOrderStatus(OrderStatus.PROCESSING);
        order.setTotalAmount(200);
        order.setTotalQuantity(2);

        orderResponse = new OrderResponse();
        orderResponse.setId(1L);
        orderResponse.setOrderStatus(OrderStatus.PROCESSING);
    }

    @Test
    void testCreateOrder_Success() {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setName("Product A");

        when(restTemplate.getForObject(anyString(), eq(ProductResponse.class)))
                .thenReturn(productResponse);
        when(modelMapper.map(orderRequest, Order.class)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(modelMapper.map(order, OrderResponse.class)).thenReturn(orderResponse);

        OrderResponse response = orderService.createOrder(orderRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testGetOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(modelMapper.map(order, OrderResponse.class)).thenReturn(orderResponse);

        OrderResponse response = orderService.getOrder(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void testGetOrder_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrder(1L));
    }

    @Test
    void testCancelOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(modelMapper.map(order, OrderResponse.class)).thenReturn(orderResponse);

        OrderResponse response = orderService.cancelOrder(1L);

        assertEquals(OrderStatus.PROCESSING, response.getOrderStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testCancelOrder_InvalidStatus() {
        order.setOrderStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStatusException.class, () -> orderService.cancelOrder(1L));
    }

    @Test
    void testCalculateTotalAmount_Valid() {
        long amount = ReflectionTestUtils.invokeMethod(orderService, "calculateTotalAmount", orderRequest.getOrderItems());
        assertEquals(200L, amount);
    }

    @Test
    void testCalculateTotalQuantity_Valid() {
        int qty = ReflectionTestUtils.invokeMethod(orderService, "calculateTotalQuantity", orderRequest.getOrderItems());
        assertEquals(2, qty);
    }

    @Test
    void testValidateProduct_NotFound() {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setName("Unavailable");

        when(restTemplate.getForObject(anyString(), eq(ProductResponse.class)))
                .thenReturn(productResponse);

        assertThrows(ResourceNotFoundException.class, () ->
                ReflectionTestUtils.invokeMethod(orderService, "validateProduct", "P001"));
    }
}
