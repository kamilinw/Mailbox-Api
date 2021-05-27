package pl.kamilwnek.mailbox.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.kamilwnek.mailbox.config.VerificationTokenConfig;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@AllArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final VerificationTokenConfig verificationTokenConfig;

    @Async
    public void send(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm Your Email");
            helper.setFrom(verificationTokenConfig.getEmailSender());

            mailSender.send(mimeMessage);
        } catch (MessagingException e){
            log.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }
}
