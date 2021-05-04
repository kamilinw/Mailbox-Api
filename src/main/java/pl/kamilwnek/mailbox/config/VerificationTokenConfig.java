package pl.kamilwnek.mailbox.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application.verification")
public class VerificationTokenConfig {
    private String emailSender;
    private String verificationLinkPrefix;

    public VerificationTokenConfig() {
        // empty on purpose
    }

    public void setEmailSender(String emailSender) {
        this.emailSender = emailSender;
    }

    public void setVerificationLinkPrefix(String verificationLinkPrefix) {
        this.verificationLinkPrefix = verificationLinkPrefix;
    }

    public String getEmailSender() {
        return emailSender;
    }

    public String getVerificationLinkPrefix() {
        return verificationLinkPrefix;
    }
}
