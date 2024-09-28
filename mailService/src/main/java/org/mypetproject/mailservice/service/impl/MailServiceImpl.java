package org.mypetproject.mailservice.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mypetproject.mailservice.dto.RecipeSaveMessage;
import org.mypetproject.mailservice.service.MailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.mypetproject.mailservice.config.RabbitMQConfig.RECIPE_SAVE_EMAIL_QUEUE;
import static org.mypetproject.mailservice.config.RabbitMQConfig.REGISTRATION_EMAIL_QUEUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {
    @Value("${spring.mail.username}")
    private String mailFrom;
    @Autowired
    private SpringTemplateEngine templateEngine;

    private final JavaMailSender mailSender;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = REGISTRATION_EMAIL_QUEUE)
    public void sendRegistrationEmail(String email) {
        Context context = new Context();
        context.setVariable("title", "Registration Successful");

        String htmlContent = templateEngine.process("registrationEmail", context);
        sendHtmlEmail(email, "Registration Successful", htmlContent);
    }

    @RabbitListener(queues = RECIPE_SAVE_EMAIL_QUEUE)
    public void sendRecipeSaveEmail(RecipeSaveMessage message) {
        Context context = new Context();
        context.setVariable("title", "Recipe Saved");
        context.setVariable("recipeTitle", message.getRecipeTitle());

        String htmlContent = templateEngine.process("recipeSaveEmail", context);
        sendHtmlEmail(message.getEmail(), "Recipe Saved", htmlContent);
    }

    // Новый метод для отправки HTML-писем
    public void sendHtmlEmail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Включаем поддержку HTML

            helper.setFrom(mailFrom);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Failed to send email to: " + to, e);
        }
    }
}