package com.virtual.herbal.garden.Virtual_Herbs_Garden.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plantName;
    private String scientificName;
    private String uses;
    private String region;
    private String image3DUrl;

    @Column(length = 2000)
    private String description;

    private String voiceDescriptionUrl;
    private String buyingLink;
    private String imageUrl;
    private String plantType;
    private String createdBy;
}
