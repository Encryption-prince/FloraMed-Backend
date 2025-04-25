package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;


import com.virtual.herbal.garden.Virtual_Herbs_Garden.dto.CreateBlogRequest;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.dto.FilterBlogRequest;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Blog;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.BlogRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BlogController {

    private final BlogRepository blogRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/add")
    public ResponseEntity<?> createBlog(@ModelAttribute CreateBlogRequest request, HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid token");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String authorEmail = jwtUtil.getEmailFromToken(token);


        Blog blog = Blog.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(authorEmail)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        blogRepository.save(blog);
        return ResponseEntity.ok("Blog posted successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Blog>> getAllBlogs() {
        return ResponseEntity.ok(blogRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBlogById(@PathVariable Long id) {
        return blogRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/filter")
    public ResponseEntity<List<Blog>> filterBlogs(@RequestBody FilterBlogRequest filterRequest) {
        if (StringUtils.hasText(filterRequest.getTitle()) && StringUtils.hasText(filterRequest.getAuthor())) {
            return ResponseEntity.ok(blogRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(
                    filterRequest.getTitle(), filterRequest.getAuthor()));
        } else if (StringUtils.hasText(filterRequest.getTitle())) {
            return ResponseEntity.ok(blogRepository.findByTitleContainingIgnoreCase(filterRequest.getTitle()));
        } else if (StringUtils.hasText(filterRequest.getAuthor())) {
            return ResponseEntity.ok(blogRepository.findByAuthorContainingIgnoreCase(filterRequest.getAuthor()));
        } else if (StringUtils.hasText(filterRequest.getContent())) {
            return ResponseEntity.ok(blogRepository.findByContentContainingIgnoreCase(filterRequest.getContent()));
        } else {
            return ResponseEntity.ok(blogRepository.findAll());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBlog(@PathVariable Long id, @RequestBody CreateBlogRequest request, HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid token");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String email = jwtUtil.getEmailFromToken(token);
        return blogRepository.findById(id).map(blog -> {
            if (!blog.getAuthor().equals(email)) {
                return ResponseEntity.status(403).body("You are not authorized to update this blog");
            }
            blog.setTitle(request.getTitle());
            blog.setContent(request.getContent());
            blog.setUpdatedAt(LocalDateTime.now());
            blogRepository.save(blog);
            return ResponseEntity.ok("Blog updated successfully");
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable Long id, HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid token");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String email = jwtUtil.getEmailFromToken(token);
        return blogRepository.findById(id).map(blog -> {
            if (!blog.getAuthor().equals(email)) {
                return ResponseEntity.status(403).body("You are not authorized to delete this blog");
            }
            blogRepository.delete(blog);
            return ResponseEntity.ok("Blog deleted successfully");
        }).orElse(ResponseEntity.notFound().build());
    }


}
