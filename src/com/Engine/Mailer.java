package com.Engine;

import com.sun.mail.smtp.SMTPTransport;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Properties;

public class Mailer {

    private static String msg;

    public static boolean SendMail() {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        try {

            Session session = Session.getInstance(prop, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("tradereports188@gmail.com", "Hotdog123@");
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("tradereports118@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(""
                            + "xaes@protonmail.com"
                            + ",quantumbiology@hotmail.co.uk"
                            + ",bugfind@xs4all.nl"
                            + ",yarcisan@gmail.com"
                    ));
            message.setSubject("Trade Report");

            String msg = Mailer.msg;

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
        return true;
    }

    public static void setMsg(String msg) {
        Mailer.msg = msg;
    }

    public static void main(String[] args) {
        Mailer.setMsg("Hello 123");
        Mailer.SendMail();
    }
}