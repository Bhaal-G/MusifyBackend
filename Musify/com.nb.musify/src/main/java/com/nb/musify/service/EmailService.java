package com.nb.musify.service;

public interface EmailService {
    void sendCredentialEmail(String toEmail,String userName, String password);

    void sendWelcomeEmail(String toEmail,String userName,String password);
}
