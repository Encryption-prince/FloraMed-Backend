package com.virtual.herbal.garden.Virtual_Herbs_Garden.repository;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.ProductPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductPurchaseRepository extends JpaRepository<ProductPurchase, Long> {
    List<ProductPurchase> findByUserEmail(String userEmail);
    List<ProductPurchase> findByProductId(Long productId);
    boolean existsByProductIdAndUserEmail(Long productId, String userEmail);
}

