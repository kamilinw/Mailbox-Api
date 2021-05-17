package pl.kamilwnek.mailbox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.kamilwnek.mailbox.validator.UniqueEmailValue;
import pl.kamilwnek.mailbox.validator.UniqueUsernameValue;

import javax.validation.constraints.Email;

@AllArgsConstructor
@Getter
public class RegistrationRequest {

    @UniqueUsernameValue
    private final String username;

    private final String password;

    @Email
    @UniqueEmailValue
    private final String email;

}
