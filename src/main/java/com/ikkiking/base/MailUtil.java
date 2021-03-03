package com.ikkiking.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
public class MailUtil {

    /**
     * Отправка письма для восстановления пароля
     *
     * @param emailSender Объект для отправки сообщения
     * @param from ящик с которого идёт рассылка
     * @param to ящик на которой отправляется письмо
     * @param subject тема письма
     * @param text текст письма
     * */
    public static void sendMail(JavaMailSender emailSender,
                                String from,
                                String to,
                                String subject,
                                String text) throws MailAuthenticationException {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
        log.info("Email is sended!");
    }
}