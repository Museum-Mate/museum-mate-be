package com.dev.museummate.utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailUtils {

    private static String id; // gmail 계정

    private static String password;   // gmail 패스워드

    @Value("${mail.sender.id}")
    public void setId(String id){
        this.id = id;
    }

    @Value("${mail.sender.password}")
    public void setPassword(String password){
        this.password = password;
    }

    public static MimeMessage mailConfiguration() {

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", 465);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(id, password);
            }
        });

        return new MimeMessage(session);

    }

    public static void bookmarkMailSend(String toEmailAddress, String userName, String exhibitionName, Integer leftDate){

        try{
            MimeMessage message = mailConfiguration();
            message.setFrom(new InternetAddress(id));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmailAddress));

            message.setSubject(String.format("[Museum Mate]: 전시 일정 안내")); //메일 제목을 입력

            message.setText(String.format("%s님께서 북마크한 전시 [%s] 마감 %d일 전입니다!", userName, exhibitionName, leftDate));    //메일 내용을 입력

            Transport.send(message);
            log.info("mail 전송 완료");

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}