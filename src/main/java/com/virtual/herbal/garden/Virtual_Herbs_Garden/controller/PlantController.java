package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.dto.PlantDto;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Plant;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.PlantRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.security.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/plants")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Plant API")
public class PlantController {
    private final PlantRepository plantRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/add")
    public ResponseEntity<?> addPlant(
            @RequestBody PlantDto plantDto,
            HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String jwt = authHeader.substring(7);
        if (!jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String createdBy = jwtUtil.getEmailFromToken(jwt);

        Plant plant = Plant.builder()
                .plantName(plantDto.getPlantName())
                .description(plantDto.getDescription())
                .region(plantDto.getRegion())
                .image3DUrl(plantDto.getImage3DUrl())
                .createdBy(createdBy)
                .voiceDescriptionUrl(plantDto.getVoiceDescriptionUrl())
                .buyingLink(plantDto.getBuyingLink())
                .scientificName(plantDto.getScientificName())
                .uses(plantDto.getUses())
                .imageUrl(plantDto.getImageUrl())
                .plantType(plantDto.getPlantType())
                .build();

        plantRepository.save(plant);

        return ResponseEntity.ok("Plant added successfully.");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Plant>> getAllPlants() {
        return ResponseEntity.ok(plantRepository.findAll());
    }

    @GetMapping("/by-region")
    public ResponseEntity<List<Plant>> getPlantsByRegion(@RequestParam String region) {
        return ResponseEntity.ok(plantRepository.findByRegion(region));
    }

    @GetMapping("/my-plants")
    public ResponseEntity<?> getMyPlants(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String jwt = authHeader.substring(7);
        if (!jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String email = jwtUtil.getEmailFromToken(jwt);
        List<Plant> myPlants = plantRepository.findByCreatedBy(email);

        return ResponseEntity.ok(myPlants);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Plant>> filterPlants(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String plantType,
            @RequestParam(required = false) String uses,
            @RequestParam(required = false) String scientificName,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String createdBy) {

        List<Plant> filteredPlants = plantRepository.filterPlants(
                region, plantType, uses, scientificName, name, createdBy);

        return ResponseEntity.ok(filteredPlants);
    }


}
