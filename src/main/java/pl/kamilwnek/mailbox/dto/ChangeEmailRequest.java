package pl.kamilwnek.mailbox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.kamilwnek.mailbox.validator.UniqueEmailValue;

import javax.validation.constraints.Email;

@AllArgsConstructor
@Getter
@Setter
public class ChangeEmailRequest {
    @Email
    private String oldEmail;

    @Email
    @UniqueEmailValue
    private String newEmail;
}
