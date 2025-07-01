package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.service.MailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test/mail")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Mail-Test-Controller")
public class MailTestController {

    private final MailService mailService;

    @GetMapping("/send")
    public ResponseEntity<String> sendTestMail(@RequestParam String to) {
        mailService.sendApprovalEmail(to);
        return ResponseEntity.ok("Test mail sent to " + to);
    }
}
