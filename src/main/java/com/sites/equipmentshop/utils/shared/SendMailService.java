package com.sites.equipmentshop.utils.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

@Service
public class SendMailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendMailService.class);


    private final String emailLogin;
    private final String emailPassword;

    public static SendMailService sendMail(String emailLogin, String emailPassword){
        return new SendMailService(emailLogin, emailPassword);
    }

    private SendMailService( @Value("${equipments.security.email.login}") String emailLogin,   @Value("${equipments.security.email.password}") String emailPassword) {
        this.emailLogin = emailLogin;
        this.emailPassword = emailPassword;
    }

    public void sendMessage(String toEmail, String subject, String body) throws MessagingException {
        Message message = baseConfiguration();
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setContent(body, "text/html");
        Transport.send(message);
    }

    private Message baseConfiguration() throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = getSession(props);
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailLogin));
        return message;
    }

    public void sendMessageMoreThanOne(String subject, String body, String... toEmail) throws MessagingException {
        Message message = baseConfiguration();
        final InternetAddress[] toEmails = Stream.of(toEmail)
                .map(email -> {
                    try {
                        return new InternetAddress(email);
                    } catch (AddressException e) {
                        LOGGER.error("Can't build InternetAddress for email: {}", email, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(InternetAddress[]::new);

        message.setRecipients(Message.RecipientType.TO, toEmails);
        message.setSubject(subject);
        message.setContent(body, "text/html");
        Transport.send(message);
    }

    private Session getSession(Properties props) {
        return Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailLogin, emailPassword);
            }
        });
    }

}
