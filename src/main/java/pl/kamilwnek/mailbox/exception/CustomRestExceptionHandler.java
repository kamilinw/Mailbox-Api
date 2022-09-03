package pl.kamilwnek.mailbox.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        List<String> errors = new ArrayList<>();

        errors.addAll(ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList()));
        errors.addAll(ex.getBindingResult().getGlobalErrors()
                .stream()
                .map(err -> err.getObjectName() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList()));

        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, "", errors, request.getContextPath());
        return handleExceptionInternal(
                ex, apiError, headers, apiError.getStatus(), request);
    }

    @ExceptionHandler(value = WrongUsernameException.class)
    protected ResponseEntity<Object> handleValidationException(
            RuntimeException ex, WebRequest request) {

        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, "", ex.getMessage(), request.getContextPath());

        return handleExceptionInternal(ex, apiError, new HttpHeaders(), apiError.getStatus(), request);
    }

    @ExceptionHandler(value = NoMailboxForThisUserException.class)
    protected ResponseEntity<Object> handleNoSuchRecordException(
            RuntimeException ex, WebRequest request) {

        ApiError apiError =
                new ApiError(HttpStatus.NOT_FOUND, "", ex.getMessage(), request.getContextPath());

        return handleExceptionInternal(ex, apiError, new HttpHeaders(), apiError.getStatus(), request);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    protected ResponseEntity<Object> handleAuthException(
            RuntimeException ex, WebRequest request) {

        ApiError apiError =
                new ApiError(HttpStatus.UNAUTHORIZED, "", ex.getMessage(), request.getContextPath());

        return handleExceptionInternal(ex, apiError, new HttpHeaders(), apiError.getStatus(), request);
    }

}
