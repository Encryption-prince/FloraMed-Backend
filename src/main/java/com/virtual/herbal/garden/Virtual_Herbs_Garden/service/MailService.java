package com.virtual.herbal.garden.Virtual_Herbs_Garden.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendUnBannedEmail(String to) {
        sendEmail(to, "Account Unbanned ✅", "Your account has been restored, Have a nice experience.");
    }

    public void sendBannedEmail(String to) {
        sendEmail(to, "Your Account has been banned", "Due to violating user policy, your account has been banned. If you think this is a mistake, please contact support.");
    }

//    public void sendPendingEmail(String to) {
//        sendEmail(to, "Herbalist Application Pending ⏳", "Your herbalist application is under review.");
//    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sample.email@gmail.com");  // your sender address
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}


