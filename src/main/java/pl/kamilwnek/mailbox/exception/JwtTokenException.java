package pl.kamilwnek.mailbox.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class JwtTokenException extends RuntimeException{
    public JwtTokenException(String message){
        super(message);
    }
}
