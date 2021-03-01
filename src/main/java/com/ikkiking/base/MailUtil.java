package com.ikkiking.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
public class MailUtil {
    public static void sendMail(JavaMailSender emailSender,
                                String from,
                                String to,
                                String subject,
                                String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
        log.info("Email is sended!");
    }
}