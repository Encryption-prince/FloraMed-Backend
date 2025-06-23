package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.ProductReview;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.ProductPurchaseRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.ProductReviewRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@CrossOrigin(origins = "*")
@Tag(name = "Product Reviews and Ratings API")
public class ProductReviewController {

    private final ProductReviewRepository reviewRepo;
    private final ProductPurchaseRepository purchaseRepo;

    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody ProductReview review) {
        // Check if user purchased product
        boolean hasPurchased = purchaseRepo.existsByProductIdAndUserEmail(
                review.getProductId(), review.getUserEmail());

        if (!hasPurchased) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only review products you've purchased.");
        }

        // Check if user already reviewed
        if (reviewRepo.findByProductIdAndUserEmail(review.getProductId(), review.getUserEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("You have already reviewed this product.");
        }

        review.setReviewedAt(LocalDateTime.now());
        reviewRepo.save(review);
        return ResponseEntity.ok("Review added successfully");
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<?> getReviewsByProduct(@PathVariable Long productId) {
        List<ProductReview> reviews = reviewRepo.findByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/average-rating/{productId}")
    public ResponseEntity<?> getAverageRating(@PathVariable Long productId) {
        List<ProductReview> reviews = reviewRepo.findByProductId(productId);

        double avg = reviews.stream()
                .mapToInt(ProductReview::getRating)
                .average()
                .orElse(0.0);

        return ResponseEntity.ok(avg);
    }
}

