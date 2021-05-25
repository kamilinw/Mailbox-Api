package pl.kamilwnek.mailbox.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Wrong username")
public class WrongUsernameException extends RuntimeException {
    public WrongUsernameException(String message) {
        super(message);
    }
}
