package pl.kamilwnek.mailbox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.kamilwnek.mailbox.validator.NotStartWithMailbox;
import pl.kamilwnek.mailbox.validator.UniqueEmailValue;
import pl.kamilwnek.mailbox.validator.UniqueUsernameValue;

import javax.validation.constraints.Email;

@AllArgsConstructor
@Getter
public class RegistrationRequest {

    @UniqueUsernameValue
    @NotStartWithMailbox
    private final String username;

    private final String password;


    @UniqueEmailValue
    @Email
    private final String email;

}
