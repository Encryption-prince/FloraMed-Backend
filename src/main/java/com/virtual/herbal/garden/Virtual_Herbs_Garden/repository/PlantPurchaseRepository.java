package com.virtual.herbal.garden.Virtual_Herbs_Garden.repository;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.PlantPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantPurchaseRepository extends JpaRepository<PlantPurchase, Long> {
    List<PlantPurchase> findByUserEmail(String userEmail);
    List<PlantPurchase> findByPlantId(Long herbId);
}

