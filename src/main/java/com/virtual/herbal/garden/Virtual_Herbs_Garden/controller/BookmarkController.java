package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.BookmarkedPlant;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Plant;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.BookmarkedPlantRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.PlantRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.security.jwt.JwtUtil;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.service.BookmarkService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Bookmark Plant API")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;
    private final BookmarkedPlantRepository bookmarkRepo;
    private final PlantRepository plantRepo;
    private final JwtUtil jwtUtil;

    @PostMapping("/add/{plantId}")
    public ResponseEntity<?> bookmarkPlant(@PathVariable Long plantId, HttpServletRequest request) {
        String email = extractEmail(request);
        if (email == null) return ResponseEntity.status(401).body("Unauthorized");

        if (bookmarkRepo.existsByUserEmailAndPlant_Id(email, plantId)) {
            return ResponseEntity.badRequest().body("Already bookmarked");
        }

        Plant plant = plantRepo.findById(plantId).orElse(null);
        if (plant == null) return ResponseEntity.badRequest().body("Plant not found");

        BookmarkedPlant bookmark = BookmarkedPlant.builder()
                .userEmail(email)
                .plant(plant)
                .build();

        bookmarkRepo.save(bookmark);
        return ResponseEntity.ok("Bookmarked successfully");
    }

//    @DeleteMapping("/remove/{plantId}")
//    public ResponseEntity<?> removeBookmark(@PathVariable Long plantId, HttpServletRequest request) {
//        String email = extractEmail(request);
//        if (email == null) return ResponseEntity.status(401).body("Unauthorized");
//
//        bookmarkRepo.deleteByUserEmailAndPlant_Id(email, plantId);
//        return ResponseEntity.ok("Bookmark removed");
//    }

    @DeleteMapping("/remove/{plantId}")
    public ResponseEntity<String> removeBookmark(@PathVariable Long plantId, HttpServletRequest request) {
        String email = extractEmail(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        bookmarkService.removeBookmark(email, plantId);
        return ResponseEntity.ok("Bookmark removed successfully");
    }


    @GetMapping("/my")
    public ResponseEntity<?> getMyBookmarks(HttpServletRequest request) {
        String email = extractEmail(request);
        if (email == null) return ResponseEntity.status(401).body("Unauthorized");

        List<BookmarkedPlant> bookmarks = bookmarkRepo.findByUserEmail(email);
        return ResponseEntity.ok(bookmarks);
    }

    private String extractEmail(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) return null;

        String jwt = authHeader.substring(7);
        if (!jwtUtil.validateToken(jwt)) return null;

        return jwtUtil.getEmailFromToken(jwt);
    }
}
