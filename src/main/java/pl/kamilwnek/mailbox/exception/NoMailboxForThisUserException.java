package pl.kamilwnek.mailbox.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "This user has no mailboxes")
public class NoMailboxForThisUserException extends RuntimeException {
    public NoMailboxForThisUserException(String message) {
        super(message);
    }
}
