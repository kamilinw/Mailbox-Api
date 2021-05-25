package pl.kamilwnek.mailbox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateMailboxRequest {

    private final String username;
    private final String password;
    private final String name;

}
