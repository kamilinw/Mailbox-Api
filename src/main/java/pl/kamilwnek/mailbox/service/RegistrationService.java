package pl.kamilwnek.mailbox.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import pl.kamilwnek.mailbox.config.VerificationTokenConfig;
import pl.kamilwnek.mailbox.dto.RegistrationRequest;
import pl.kamilwnek.mailbox.model.User;
import pl.kamilwnek.mailbox.security.ApplicationUserRole;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service
public class RegistrationService {

    private final EmailService emailService;
    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final VerificationTokenConfig verificationTokenConfig;
    private final SpringTemplateEngine templateEngine;

    public String register(RegistrationRequest request) {
        // user's username can not start with "mailbox" because its reserved for mailbox users
        if (request.getUsername().startsWith("mailbox")){
            throw new IllegalArgumentException("Username can not start with mailbox");
        }

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
        emailService.send(request.getEmail(), buildEmail(request.getUsername(), link));

        return token;
    }


    @Transactional
    public String confirm(String token) {
        var confirmationToken = confirmationTokenService
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

    private String buildEmail(String name, String link) {
        Context context = new Context();
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);
        variables.put("link", link);
        context.setVariables(variables);

        return templateEngine.process("email/email-template", context);

    }
}
