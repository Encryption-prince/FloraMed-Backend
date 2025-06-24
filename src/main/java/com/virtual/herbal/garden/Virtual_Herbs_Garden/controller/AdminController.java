package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.Role;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.User;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.entity.VerificationStatus;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.repository.UserRepository;
import com.virtual.herbal.garden.Virtual_Herbs_Garden.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepo;
    private final MailService mailService;

    // 🔍 Get all pending herbalists
    @GetMapping("/herbalists/pending")
    public List<User> getPendingHerbalists() {
        return userRepo.findByRoleAndVerificationStatus(Role.HERBALIST, VerificationStatus.PENDING);
    }

    // ✅ Approve herbalist
    @PutMapping("/herbalists/{id}/approve")
    public ResponseEntity<String> approveHerbalist(@PathVariable Long id) {
        User herbalist = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        herbalist.setVerificationStatus(VerificationStatus.APPROVED);
        userRepo.save(herbalist);
        mailService.sendApprovalEmail(herbalist.getEmail());
        return ResponseEntity.ok("Herbalist approved ✅");
    }

    // ❌ Reject herbalist
    @PutMapping("/herbalists/{id}/reject")
    public ResponseEntity<String> rejectHerbalist(@PathVariable Long id) {
        User herbalist = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        mailService.sendRejectionEmail(herbalist.getEmail());
        userRepo.delete(herbalist);
        return ResponseEntity.ok("Herbalist rejected and deleted ❌");
    }


    // 🚫 Ban a user or herbalist
    @PutMapping("/users/{id}/ban")
    public ResponseEntity<String> banUser(@PathVariable Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBanned(true);
        userRepo.save(user);
        return ResponseEntity.ok("User " + user.getEmail() + " has been banned 🚫");
    }

    // ✅ Unban a user or herbalist
    @PutMapping("/users/{id}/unban")
    public ResponseEntity<String> unbanUser(@PathVariable Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBanned(false);
        userRepo.save(user);
        return ResponseEntity.ok("User " + user.getEmail() + " has been unbanned ✅");
    }

    // 🔍 Get all banned users
    @GetMapping("/users/banned")
    public List<User> getBannedUsers() {
        return userRepo.findByBannedTrue();
    }

}

