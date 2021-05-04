package pl.kamilwnek.mailbox.dto;

import org.springframework.beans.factory.annotation.Autowired;
import pl.kamilwnek.mailbox.validator.UniqueEmailValue;
import pl.kamilwnek.mailbox.validator.UniqueUsernameValue;

import javax.validation.constraints.Email;

public class RegistrationRequest {

    @UniqueUsernameValue
    private final String username;

    private final String password;

    @Email
    @UniqueEmailValue
    private final String email;

    @Autowired
    public RegistrationRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
