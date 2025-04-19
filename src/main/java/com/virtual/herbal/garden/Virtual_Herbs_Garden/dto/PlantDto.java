package com.virtual.herbal.garden.Virtual_Herbs_Garden.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlantDto {
    private String plantName;
    private String scientificName;
    private String uses;
    private String region;
    private String image3DUrl;
    private String description;
    private String voiceDescriptionUrl; // NEW
    private String buyingLink;
    private String imageUrl;
    private String plantType;
}
