package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Role;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.User;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.UserRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/complete-signup")
    public ResponseEntity<?> completeSignup(@RequestParam("token") String tempToken,
                                            @RequestParam("role") Role role,
                                            @RequestPart(value = "document", required = false) String document) throws IOException {

        System.out.println("✅ Hit completeSignup endpoint");
        if (!jwtUtil.validateToken(tempToken)) {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }

        String email = jwtUtil.getClaimFromToken(tempToken, "email");
        String name = jwtUtil.getClaimFromToken(tempToken, "name");
        String picture = jwtUtil.getClaimFromToken(tempToken, "picture");

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setProfilePictureUrl(picture);
        user.setRole(role);

        if (role == Role.HERBALIST && document != null) {
            user.setIdentificationDocument(document);
        }

        userRepository.save(user);

        String jwt = jwtUtil.generateToken(email);
        return ResponseEntity.ok().body("{\"token\": \"" + jwt + "\"}");
    }

    @GetMapping("/test-token")
    public ResponseEntity<?> testToken(@RequestParam("token") String token) {
        return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
    }

}
