package com.ecom_microservices.order_service.controller;

import com.ecom_microservices.order_service.dto.request.OrderRequest;
import com.ecom_microservices.order_service.dto.response.OrderResponse;
import com.ecom_microservices.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest)
    {
        log.info("Received request to create order for customer: {}", orderRequest.getCustomerIdentifier());
        OrderResponse orderResponse=orderService.createOrder(orderRequest);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders()
    {
        log.info("Received request to get all orders");
        List<OrderResponse> orderResponses=orderService.getAllOrders();
        return new ResponseEntity<>(orderResponses,HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable long orderId)
    {
        log.info("Received request to get order with ID: {}", orderId);
        OrderResponse orderResponse=orderService.getOrder(orderId);
        return new ResponseEntity<>(orderResponse,HttpStatus.OK);
    }

    @GetMapping("/customer")
    public ResponseEntity<List<OrderResponse>> getOrderByCustomerId(@RequestParam long customerId)
    {
        log.info("Received request to get orders for customer ID: {}", customerId);
        List<OrderResponse> orderResponses=orderService.getOrderByCustomerId(customerId);
        return new ResponseEntity<>(orderResponses,HttpStatus.OK);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable long orderId,@Valid @RequestBody OrderRequest orderRequest)
    {
        log.info("Received request to update order with ID: {}", orderId);
        OrderResponse orderResponse=orderService.updateOrder(orderId,orderRequest);
        return new ResponseEntity<>(orderResponse,HttpStatus.OK);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteByOrder(@PathVariable long orderId)
    {
        log.info("Received request to delete order with ID: {}", orderId);
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/cancel/{orderId}")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable long orderId) {
        log.info("Received request to cancel order with ID: {}", orderId);
        OrderResponse orderResponse = orderService.cancelOrder(orderId);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }

    @PutMapping("/status/{orderId}")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        log.info("Received request to change order status with ID: {}", orderId);
        OrderResponse orderResponse= orderService.updateOrderStatus(orderId, status);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }

}
