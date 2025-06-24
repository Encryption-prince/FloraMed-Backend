package com.virtual.herbal.garden.Virtual_Herbs_Garden.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendApprovalEmail(String to) {
        sendEmail(to, "Herbalist Application Approved ✅", "Congratulations! Your herbalist profile has been approved.");
    }

    public void sendRejectionEmail(String to) {
        sendEmail(to, "Herbalist Application Rejected ❌", "Sorry, your herbalist application was not approved.");
    }

    public void sendPendingEmail(String to) {
        sendEmail(to, "Herbalist Application Pending ⏳", "Your herbalist application is under review.");
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your.email@gmail.com");  // your sender address
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}


