package com.ecom_microservices.payment_service.repository;


import com.ecom_microservices.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderId(String orderId);
}
