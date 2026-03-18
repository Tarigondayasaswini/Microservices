package com.revplay.userservice.service;

public interface EmailService {
    void sendEmail(String to, String subject, String text);

    void sendWelcomeEmail(String to, String name);
}
