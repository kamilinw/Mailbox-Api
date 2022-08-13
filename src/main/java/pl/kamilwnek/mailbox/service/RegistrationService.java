package pl.kamilwnek.mailbox.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kamilwnek.mailbox.config.VerificationTokenConfig;
import pl.kamilwnek.mailbox.dto.RegistrationRequest;
import pl.kamilwnek.mailbox.model.ConfirmationToken;
import pl.kamilwnek.mailbox.model.User;
import pl.kamilwnek.mailbox.security.ApplicationUserRole;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
@Service
public class RegistrationService {

    private final EmailService emailService;
    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final VerificationTokenConfig verificationTokenConfig;

    public String register(RegistrationRequest request) {
        String token = userService.signUpUser(
                new User(
                        request.getUsername(),
                        request.getPassword(),
                        request.getEmail(),
                        ApplicationUserRole.USER
                )
        );

        String link = verificationTokenConfig.getVerificationLinkPrefix() + token;
        log.info("in RegistrationService.register(). sent link:" + link + " to " + request.getEmail());
        emailService.sendEmail(
                request.getEmail(),
                emailService.buildConfirmationEmail(request.getUsername(), link),
                "Confirm Your Email");

        return token;
    }


    @Transactional
    public String confirm(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        userService.enableAppUser(confirmationToken.getUser().getUsername());
        return "confirmed";
    }
}
