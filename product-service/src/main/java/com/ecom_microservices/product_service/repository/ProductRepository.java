package com.ecom_microservices.product_service.repository;

import com.ecom_microservices.product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductId(String productId);
    boolean existsByProductId(String productId);
    void deleteByProductId(String productId);
    List<Product> findByCategory(String category);
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findByPriceBetween(double minPrice, double maxPrice);
    Page<Product> findAll(Pageable pageable);
}
