package pl.kamilwnek.mailbox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.kamilwnek.mailbox.model.Mailbox;

import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
public class UserResponse {
    private String username;
    private String email;
    private Set<Mailbox> mailboxes;
}
