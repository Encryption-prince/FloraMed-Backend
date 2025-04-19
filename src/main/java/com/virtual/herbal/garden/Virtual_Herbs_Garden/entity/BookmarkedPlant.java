package com.virtual.herbal.garden.Virtual_Herbs_Garden.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookmarked_plants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkedPlant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    @ManyToOne
    @JoinColumn(name = "plant_id")
    private Plant plant;
}
