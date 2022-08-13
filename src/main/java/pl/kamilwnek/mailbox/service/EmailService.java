package pl.kamilwnek.mailbox.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import pl.kamilwnek.mailbox.config.VerificationTokenConfig;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@AllArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final VerificationTokenConfig verificationTokenConfig;
    private final SpringTemplateEngine templateEngine;
    private Environment env;

    @Async
    public void sendEmailOld(String to, String email, String subject) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(verificationTokenConfig.getEmailSender());

            mailSender.send(mimeMessage);
        } catch (MessagingException e){
            log.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }

    @Async
    public void sendEmail(String to, String email, String subject){
        Properties prop = new Properties();
        prop.put("mail.smtp.host", env.getProperty("spring.mail.host"));
        prop.put("mail.smtp.port", env.getProperty("spring.mail.port"));
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(env.getProperty("spring.mail.username"), env.getProperty("spring.mail.password"));
                    }
                });

        try {

            MimeMessage message = new MimeMessage(session);
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(new InternetAddress(verificationTokenConfig.getEmailSender()));

            Transport.send(message);


        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    public String buildConfirmationEmail(String name, String link) {
        Context context = new Context();
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("link", link);
        context.setVariables(variables);

        return templateEngine.process("email/email-template", context);
    }

    public String buildNewLetterEmail(String date) {
        Context context = new Context();
        Map<String, Object> variables = new HashMap<>();
        variables.put("date", date);
        context.setVariables(variables);

        return templateEngine.process("email/new-letter-email-template", context);
    }
}
