package org.mypetproject.mailservice.service;

public interface MailService {
    void sendHtmlEmail(String to, String subject, String body);
}
