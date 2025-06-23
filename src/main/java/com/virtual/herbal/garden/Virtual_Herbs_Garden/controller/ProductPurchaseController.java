package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.ProductPurchase;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.ProductPurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchases")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductPurchaseController {

    private final ProductPurchaseRepository purchaseRepo;

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
    @GetMapping("/by-product")
    public ResponseEntity<List<ProductPurchase>> getPurchasesByProduct(@RequestParam Long productId) {
        return ResponseEntity.ok(purchaseRepo.findByProductId(productId));
    }

}
