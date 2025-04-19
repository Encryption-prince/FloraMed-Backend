package com.virtual.herbal.garden.Virtual_Herbs_Garden.service;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.dto.PlantDto;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Plant;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Role;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.User;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.PlantRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlantService {
    private final PlantRepository plantRepository;
    private final UserRepository userRepository;

    public ResponseEntity<?> addPlant(PlantDto plantDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().equals(Role.HERBALIST)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only herbalists can add plants.");
        }

        Plant plant = new Plant(
                null,
                plantDto.getPlantName(),
                plantDto.getScientificName(),
                plantDto.getUses(),
                plantDto.getRegion(),
                plantDto.getImage3DUrl(),
                plantDto.getDescription(),
                plantDto.getVoiceDescriptionUrl(),
                plantDto.getBuyingLink(),
                email
        );

        plantRepository.save(plant);
        return ResponseEntity.ok("Plant added successfully");
    }
}
