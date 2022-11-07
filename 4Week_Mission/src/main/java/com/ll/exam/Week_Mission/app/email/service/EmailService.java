package com.ll.exam.Week_Mission.app.email.service;

import com.ll.exam.Week_Mission.app.email.config.EmailConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Getter
@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailConfig emailConfig;
    private String welcomeSubject="MeotBooks 회원가입 축하 메일입니다.";
    private String welcomeMessage = "회원님 MeotBooks를 찾아주셔서 감사드립니다.";

    public void sendPlainTextEmail(String toAddress,
                                   String subject, String message) throws AddressException,
            MessagingException {

        // sets SMTP server properties
        Properties properties = emailConfig.setSmtpProperties();

        // creates a new session, no Authenticator (will connect() later)
        Session session = Session.getDefaultInstance(properties);

        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(emailConfig.getUserName()));
        InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setText(message);

        // sends the e-mail
        Transport t = session.getTransport("smtp");
        t.connect(emailConfig.getUserName(), emailConfig.getPassword());
        t.sendMessage(msg, msg.getAllRecipients());
        t.close();
    }
}