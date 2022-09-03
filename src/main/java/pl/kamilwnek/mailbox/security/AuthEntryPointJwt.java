package pl.kamilwnek.mailbox.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pl.kamilwnek.mailbox.exception.ApiError;
import pl.kamilwnek.mailbox.exception.JwtTokenException;
import pl.kamilwnek.mailbox.security.jwt.JwtTokenUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.util.ObjectUtils.isEmpty;

@Component
@RequiredArgsConstructor
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String message = getJwtValidationMessage(header);

        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED,
                "",
                message.isEmpty() ? authException.getMessage() : message,
                request.getServletPath()
        );

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), apiError);
    }

    private String getJwtValidationMessage(String header){
        if (!isEmpty(header) && header.startsWith("Bearer ")) {
            final String token = header.split(" ")[1].trim();
            try {
                jwtTokenUtil.validate(token);
            } catch (JwtTokenException e) {
                return e.getMessage();
            }
        }
        return "";
    }
}
