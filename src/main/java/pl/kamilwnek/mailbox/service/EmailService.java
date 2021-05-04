package pl.kamilwnek.mailbox.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.kamilwnek.mailbox.config.VerificationTokenConfig;
import javax.mail.MessagingException;

@Service
public class EmailService {


    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;
    private final VerificationTokenConfig verificationTokenConfig;

    @Autowired
    public EmailService(JavaMailSender mailSender, VerificationTokenConfig verificationTokenConfig) {
        this.mailSender = mailSender;
        this.verificationTokenConfig = verificationTokenConfig;
    }



    @Async
    public void send(String to, String email) {
        try {
            var mimeMessage = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm Your Email");
            helper.setFrom(verificationTokenConfig.getEmailSender());

            mailSender.send(mimeMessage);
        } catch (MessagingException e){
            LOGGER.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }
}
