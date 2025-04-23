package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.dto.UserDto;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.User;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.UserRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.security.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "USER API")
public class UserController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Get logged-in user's profile")
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String jwt = authHeader.substring(7);
        if (!jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        String email = jwtUtil.getEmailFromToken(jwt);
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        UserDto userDTO = new UserDto(
                user.getName(),
                user.getEmail(),
                user.getRole().toString(),  // Or user.getRole().getName() if applicable
                user.getProfilePictureUrl()
        );

        return ResponseEntity.ok(userDTO);
    }
}
