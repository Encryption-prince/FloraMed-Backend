package com.virtual.herbal.garden.Virtual_Herbs_Garden.repository;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlantRepository extends JpaRepository<Plant,Long> {
    List<Plant> findByCreatedBy(String email);
    List<Plant> findByRegion(String region);

    @Query("SELECT p FROM Plant p " +
            "WHERE (:region IS NULL OR p.region = :region) " +
            "AND (:plantType IS NULL OR p.plantType = :plantType) " +
            "AND (:uses IS NULL OR LOWER(p.uses) LIKE LOWER(CONCAT('%', :uses, '%'))) " +
            "AND (:scientificName IS NULL OR LOWER(p.scientificName) LIKE LOWER(CONCAT('%', :scientificName, '%'))) " +
            "AND (:name IS NULL OR LOWER(p.plantName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:createdBy IS NULL OR p.createdBy = :createdBy)")
    List<Plant> filterPlants(
            @Param("region") String region,
            @Param("plantType") String plantType,
            @Param("uses") String uses,
            @Param("scientificName") String scientificName,
            @Param("name") String name,
            @Param("createdBy") String createdBy);

}
