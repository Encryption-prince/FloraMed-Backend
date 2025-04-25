package com.virtual.herbal.garden.Virtual_Herbs_Garden.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String author; // This can be the user's email or name


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}