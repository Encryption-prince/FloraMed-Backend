package com.virtual.herbal.garden.Virtual_Herbs_Garden.controller;

import com.virtual.herbal.garden.Virtual_Herbs_Garden.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/mail")
@RequiredArgsConstructor
public class MailTestController {

    private final MailService mailService;

    @GetMapping("/send")
    public ResponseEntity<String> sendTestMail(@RequestParam String to) {
        mailService.sendApprovalEmail(to);
        return ResponseEntity.ok("Test mail sent to " + to);
    }
}
