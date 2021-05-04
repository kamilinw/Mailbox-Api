package pl.kamilwnek.mailbox.dto;

import pl.kamilwnek.mailbox.validator.UniqueUsernameValue;

public class CreateMailboxRequest {
    @UniqueUsernameValue
    private final String username;
    private final String password;

    public CreateMailboxRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
