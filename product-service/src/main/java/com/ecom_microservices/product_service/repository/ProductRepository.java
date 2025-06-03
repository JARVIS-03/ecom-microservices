package com.ecom_microservices.product_service.repository;



import com.ecom_microservices.product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductId(String productId);
    boolean existsByProductId(String productId);
    void deleteByProductId(String productId);
}