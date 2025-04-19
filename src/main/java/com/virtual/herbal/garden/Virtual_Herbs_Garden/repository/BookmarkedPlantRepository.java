package com.virtual.herbal.garden.Virtual_Herbs_Garden.repository;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.BookmarkedPlant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkedPlantRepository extends JpaRepository<BookmarkedPlant, Long> {
    List<BookmarkedPlant> findByUserEmail(String userEmail);
    boolean existsByUserEmailAndPlant_Id(String email, Long plantId);
    void deleteByUserEmailAndPlant_Id(String email, Long plantId);
}
