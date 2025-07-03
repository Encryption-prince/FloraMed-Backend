package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.dto.PlantDto;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.*;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.PlantRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.ProductRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.UserRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.service.MailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Admin-Controller")
public class AdminController {

    private final UserRepository userRepo;
    private final MailService mailService;
    private final ProductRepository productRepo;
    private final PlantRepository plantRepo;


    // 🚫 Ban a user or herbalist
    @PutMapping("/users/{id}/ban")
    public ResponseEntity<String> banUser(@PathVariable Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBanned(true);
        mailService.sendBannedEmail(user.getEmail()); // Notify user via email
        userRepo.save(user);
        return ResponseEntity.ok("User " + user.getEmail() + " has been banned 🚫");
    }

    // ✅ Unban a user or herbalist
    @PutMapping("/users/{id}/unban")
    public ResponseEntity<String> unbanUser(@PathVariable Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBanned(false);
        mailService.sendUnBannedEmail(user.getEmail()); // Notify user via email
        userRepo.save(user);
        return ResponseEntity.ok("User " + user.getEmail() + " has been unbanned ✅");
    }

    // 🔍 Get all banned users
    @GetMapping("/users/banned")
    public List<User> getBannedUsers() {
        return userRepo.findByBannedTrue();
    }

    // 🔍 Get all users
    @GetMapping("/users/all")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @GetMapping("/users/all-users")
    public ResponseEntity<List<User>> getAllUsersWithDetails() {
        List<User> users = userRepo.findAllByRole(Role.USER);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/all-herbalists")
    public ResponseEntity<List<User>> getAllHerbalistsWithDetails() {
        List<User> users = userRepo.findAllByRole(Role.HERBALIST);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(users);
    }


    //add or remove products endpoints
    // ✅ Add a new product
    @PostMapping("/add-product")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product savedProduct = productRepo.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    // ❌ Remove a product by ID
    @DeleteMapping("/{id}/product")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        if (!productRepo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found with ID: " + id);
        }
        productRepo.deleteById(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    // (Optional) 🔍 Get product by ID for admin check
    @GetMapping("/{id}/product")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ResponseEntity.ok(product);
    }

    // ✅ Add a new plant
    @PostMapping("/add/plant")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Plant> addPlant(@RequestBody PlantDto dto) {
        Plant plant = Plant.builder()
                .plantName(dto.getPlantName())
                .scientificName(dto.getScientificName())
                .uses(dto.getUses())
                .region(dto.getRegion())
                .image3DUrl(dto.getImage3DUrl())
                .description(dto.getDescription())
                .voiceDescriptionUrl(dto.getVoiceDescriptionUrl())
                .buyingLink(dto.getBuyingLink())
                .imageUrl(dto.getImageUrl())
                .plantType(dto.getPlantType())
                .createdBy("ADMIN")  // or you can fetch from current logged-in admin if available
                .build();

        Plant savedPlant = plantRepo.save(plant);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlant);
    }

    // ❌ Delete a plant
    @DeleteMapping("/{id}/plant")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deletePlant(@PathVariable Long id) {
        if (!plantRepo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No plant found with ID: " + id);
        }
        plantRepo.deleteById(id);
        return ResponseEntity.ok("Plant deleted successfully");
    }

    // ✏️ Update a plant
    @PutMapping("/{id}/plant")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> updatePlant(@PathVariable Long id, @RequestBody PlantDto dto) {
        Plant existing = plantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Plant not found"));

        existing.setPlantName(dto.getPlantName());
        existing.setScientificName(dto.getScientificName());
        existing.setUses(dto.getUses());
        existing.setRegion(dto.getRegion());
        existing.setImage3DUrl(dto.getImage3DUrl());
        existing.setDescription(dto.getDescription());
        existing.setVoiceDescriptionUrl(dto.getVoiceDescriptionUrl());
        existing.setBuyingLink(dto.getBuyingLink());
        existing.setImageUrl(dto.getImageUrl());
        existing.setPlantType(dto.getPlantType());

        Plant updated = plantRepo.save(existing);
        return ResponseEntity.ok(updated);
    }

}

