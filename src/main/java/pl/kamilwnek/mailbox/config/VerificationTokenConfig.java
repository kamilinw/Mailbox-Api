package pl.kamilwnek.mailbox.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@NoArgsConstructor
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.verification")
public class VerificationTokenConfig {
    private String emailSender;
    private String verificationLinkPrefix;

}
