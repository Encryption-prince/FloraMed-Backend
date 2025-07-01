package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Role;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.VerificationStatus;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.OrdersRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.PlantRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.ProductRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/metrics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Admin-Metrics-Controller")
public class AdminMetricsController {

    private final ProductRepository productRepo;
    private final PlantRepository plantRepo;
    private final UserRepository userRepo;
    private final OrdersRepository ordersRepo;

    @GetMapping("/overview")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        metrics.put("totalProducts", productRepo.count());
        metrics.put("totalPlants", plantRepo.count());
        metrics.put("totalUsers", userRepo.count());
        metrics.put("approvedHerbalists", userRepo.countByRoleAndVerificationStatus(Role.HERBALIST, VerificationStatus.APPROVED));
        metrics.put("pendingHerbalists", userRepo.countByRoleAndVerificationStatus(Role.HERBALIST, VerificationStatus.PENDING));
        metrics.put("totalOrders", ordersRepo.count());

        // Total revenue if amount field is in Orders
        BigDecimal totalRevenue = ordersRepo.findAll()
                .stream()
                .map(order -> BigDecimal.valueOf(order.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        metrics.put("totalRevenue", totalRevenue);

        return ResponseEntity.ok(metrics);
    }
}

