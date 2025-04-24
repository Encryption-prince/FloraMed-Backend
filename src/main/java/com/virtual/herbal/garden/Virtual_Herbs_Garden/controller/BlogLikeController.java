package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Blog;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.BlogLike;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.BlogLikeRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.BlogRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.security.jwt.JwtUtil;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.service.BlogLikeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
public class BlogLikeController {

    private final BlogLikeRepository blogLikeRepository;
    private final BlogRepository blogRepository;
    private final JwtUtil jwtUtil;

    // ✅ Like a blog
    @PostMapping("/{blogId}/like")
    public ResponseEntity<?> likeBlog(@PathVariable Long blogId, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }

        String jwt = authHeader.substring(7);
        if (!jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String email = jwtUtil.getEmailFromToken(jwt);

        if (blogLikeRepository.existsByBlogIdAndUserEmail(blogId, email)) {
            return ResponseEntity.badRequest().body("Already liked this blog.");
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        BlogLike like = BlogLike.builder()
                .blog(blog)
                .userEmail(email)
                .build();

        blogLikeRepository.save(like);

        return ResponseEntity.ok("Blog liked successfully.");
    }

    // ✅ Unlike a blog
    @DeleteMapping("/{blogId}/like")
    public ResponseEntity<?> unlikeBlog(@PathVariable Long blogId, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }

        String jwt = authHeader.substring(7);
        if (!jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String email = jwtUtil.getEmailFromToken(jwt);

        if (!blogLikeRepository.existsByBlogIdAndUserEmail(blogId, email)) {
            return ResponseEntity.badRequest().body("You haven't liked this blog yet.");
        }

        blogLikeRepository.deleteByBlogIdAndUserEmail(blogId, email);
        return ResponseEntity.ok("Blog unliked successfully.");
    }

    // ✅ Get like count for a blog (public)
    @GetMapping("/{blogId}/likes")
    public ResponseEntity<?> getLikeCount(@PathVariable Long blogId) {
        long count = blogLikeRepository.countByBlogId(blogId);
        return ResponseEntity.ok(count);
    }
}