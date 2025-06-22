package com.virtual.herbal.garden.Virtual_Herbs_Garden.repository;


import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
}
