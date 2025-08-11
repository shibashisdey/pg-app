package com.pgfinder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendVerificationEmail(String toEmail, String verificationToken, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Email Verification - PG Finder App");
        
        String verificationLink = frontendUrl + "/verify-email?token=" + verificationToken;
        
        String emailBody = String.format(
            "Dear %s,\n\n" +
            "Thank you for registering with PG Finder App. Please click the link below to verify your email address:\n\n" +
            "%s\n\n" +
            "This link will expire in 24 hours. If you didn't create an account, please ignore this email.\n\n" +
            "Best regards,\n" +
            "PG Finder App Team",
            userName, verificationLink
        );
        
        message.setText(emailBody);
        javaMailSender.send(message);
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset Request - PG Finder App");
        
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
        
        String emailBody = String.format(
            "Dear %s,\n\n" +
            "We received a request to reset your password. Click the link below to set a new password:\n\n" +
            "%s\n\n" +
            "This link will expire in 1 hour. If you didn't request this, please ignore this email.\n\n" +
            "Best regards,\n" +
            "PG Finder App Team",
            userName, resetLink
        );
        
        message.setText(emailBody);
        javaMailSender.send(message);
    }
}
