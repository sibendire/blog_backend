package com.blogger.blog.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class EmailServiceImpl implements EmailService {
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public boolean sendMimeMessage(String name, String to, String subject, String body) {
        try {
            if (to == null || to.trim().isEmpty()) {
                throw new IllegalArgumentException("Recipient email address is invalid");
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(subject);
            message.setFrom(fromEmail);
            message.setTo(to.trim());
            message.setText(body);

            mailSender.send(message);
            System.out.println("Email sent to: " + to);
            return true;
        } catch (Exception exception) {
            System.out.println("Error occurred while sending email: " + exception.getMessage());
            throw new RuntimeException("Email not sent, please try again", exception);
        }
    }

    @Override
    public void sendMimeMessageWithAttachment(String name, String to, String token) {

    }

    @Override
    public void sendMimeMessageWithEmbeddedImage(String name, String to, String token) {

    }

    @Override
    public void sendMimeMessageWithEmbeddedFile(String name, String to, String token) {

    }

    @Override
    public void sendHtmlEmail(String name, String to, String token) {

    }

    @Override
    public void sendHtmlEmailWithEmbeddedFile(String name, String to, String token) {

    }
}

