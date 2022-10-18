package com.ll.exam.Week_Mission.app.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private static final String ADMIN_ADDRESS = "alswl0w0@naver.com";

    public MimeMessage createMessage(String email, String username) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO, email);
        message.setSubject("MeotBooks 회원가입 축하 메일입니다.");

        String text="";
        text+= "<div style='margin:100px;'>";
        text+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        text+= "<h3 style='color:blue;'>" + username + "회원님 MeotBooks를 찾아주셔서 감사드립니다.</h3>";
        text+= "</div>";
        text+= "</div>";

        message.setText(text, "utf-8", "html");
        message.setFrom(new InternetAddress(ADMIN_ADDRESS,"MeotBooks"));
        return message;
    }

    @Async
    public void sendMail(MimeMessage message) throws UnsupportedEncodingException, MessagingException {
        mailSender.send(message);
    }

}