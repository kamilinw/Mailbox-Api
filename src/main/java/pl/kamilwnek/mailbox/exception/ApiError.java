package pl.kamilwnek.mailbox.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public class ApiError {
    private HttpStatus status;
    private String message;
    private List<String> errors;


    public ApiError(HttpStatus status, String message, String error) {
        super();
        this.status = status;
        this.message = message;
        errors = Collections.singletonList(error);
    }
}
