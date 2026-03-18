package com.revplay.userservice.service.impl;

import com.revplay.userservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            LOGGER.info("Email sent to {} with subject: {}", to, subject);
        } catch (Exception e) {
            LOGGER.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    @Override
    public void sendWelcomeEmail(String to, String name) {
        sendEmail(to, "Welcome to RevPlay!", "Hi " + name + ", welcome to RevPlay - your music streaming platform!");
    }
}
