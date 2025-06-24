package com.virtual.herbal.garden.Virtual_Herbs_Garden.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    // OAuth profile picture
    private String profilePictureUrl;

    // USER or HERBALIST
    @Enumerated(EnumType.STRING)
    private Role role;

    // Only set for HERBALISTs (e.g., stored on disk or S3 path)
    @Column(length = 1000)
    private String identificationDocument;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    @Column(nullable = false)
    private boolean banned = false;


}
