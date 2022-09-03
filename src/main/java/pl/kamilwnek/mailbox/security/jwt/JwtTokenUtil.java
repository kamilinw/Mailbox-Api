package pl.kamilwnek.mailbox.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.kamilwnek.mailbox.exception.JwtTokenException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    private final JwtConfig jwtConfig;
    private final JwtSecretKey jwtSecretKey;
    private final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);


    public String getTokenFromHttpAuthorizationHeader(String header){
        return header != null ? header.split(" ")[1].trim() : "";
    }

    public String getUserIdFromToken(String jwtToken){
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey.secretKey())
                .build()
                .parseClaimsJws(jwtToken);
        return claims.getBody().getSubject();
    }

    public Date getExpirationDate(String jwtToken){
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey.secretKey())
                .build()
                .parseClaimsJws(jwtToken);
        return claims.getBody().getExpiration();
    }

    public boolean validate(String token) throws JwtTokenException {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey.secretKey()).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            throw new JwtTokenException("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            throw new JwtTokenException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new JwtTokenException("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new JwtTokenException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new JwtTokenException("JWT claims string is empty");
        }
    }

}
