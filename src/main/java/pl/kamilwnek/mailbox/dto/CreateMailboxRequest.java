package pl.kamilwnek.mailbox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.kamilwnek.mailbox.validator.UniqueUsernameValue;

@AllArgsConstructor
@Getter
public class CreateMailboxRequest {
    @UniqueUsernameValue
    private final String username;
    private final String password;

}
