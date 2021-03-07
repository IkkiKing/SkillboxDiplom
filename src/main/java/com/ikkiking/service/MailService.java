package com.ikkiking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.email}")
    private String emailFromRestore;

    /**
     * Отправка письма для восстановления пароля.
     *
     * @param emailTo ящик на которой отправляется письмо
     * @param subject тема письма
     * @param message текст письма
     * */
    public void send(String emailTo,
                     String subject,
                     String message) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFromRestore);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailSender.send(mailMessage);
        log.info("Email is sended!");
    }
}
