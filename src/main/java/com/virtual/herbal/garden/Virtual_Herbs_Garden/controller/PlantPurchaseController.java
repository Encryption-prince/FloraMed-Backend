package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.PlantPurchase;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.PlantPurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
public class PlantPurchaseController {

    private final PlantPurchaseRepository purchaseRepo;

    // ✅ My Purchases - secure, only logged-in user can see their own purchases
    @GetMapping("/my-purchases")
    public ResponseEntity<?> getMyPurchases() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = auth.getName();
        return ResponseEntity.ok(purchaseRepo.findByUserEmail(email));
    }

    // ✅ Admin or Herbalist can still fetch by plant if needed (or restrict further)
    @GetMapping("/by-plant")
    public ResponseEntity<List<PlantPurchase>> getPurchasesByPlant(@RequestParam Long plantId) {
        return ResponseEntity.ok(purchaseRepo.findByPlantId(plantId));
    }

    // ✅ Log a purchase after successful payment (called internally, or by frontend after payment)
    @PostMapping("/{plantId}/buy")
    public ResponseEntity<?> logPurchase(@PathVariable Long plantId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = auth.getName();
        PlantPurchase purchase = PlantPurchase.builder()
                .plantId(plantId)
                .userEmail(email)
                .purchasedAt(LocalDateTime.now())
                .build();
        purchaseRepo.save(purchase);

        return ResponseEntity.ok("Purchase logged.");
    }
}
