package com.blogger.blog.service;

public interface EmailService {
    boolean sendMimeMessage(String name, String to, String subject, String body);

//    boolean sendMimeMessage(String name, String to, String subject, String body);

    void sendMimeMessageWithAttachment(String name, String to, String token);
    void sendMimeMessageWithEmbeddedImage(String name,String to,String token);
    void sendMimeMessageWithEmbeddedFile(String name,String to,String token);
    void sendHtmlEmail(String name,String to,String token);
    void sendHtmlEmailWithEmbeddedFile(String name,String to,String token);
}
