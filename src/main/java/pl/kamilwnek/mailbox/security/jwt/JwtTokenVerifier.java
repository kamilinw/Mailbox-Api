package pl.kamilwnek.mailbox.security.jwt;

import com.google.common.base.Strings;
import io.jsonwebtoken.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.kamilwnek.mailbox.model.User;
import pl.kamilwnek.mailbox.service.UserService;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

import java.util.*;
import java.util.stream.Collectors;

public class JwtTokenVerifier extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    private final UserService userService;
    private static final String AUTHORITIES = "authorities";
    private static final String AUTHORITY = "authority";

    @Autowired
    public JwtTokenVerifier(JwtConfig jwtConfig, SecretKey secretKey, UserService userService) {
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // getting token from header
        String authorizationHeader = request.getHeader(jwtConfig.getAuthorizationHeader());
        if (Strings.isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith(jwtConfig.getTokenPrefix())){
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorizationHeader.replace(jwtConfig.getTokenPrefix(), "");

        // decode token and get username, expirationDay and authorities
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();

        String payload = new String(decoder.decode(chunks[1]));
        JSONObject payloadJson;
        String username = null;
        Date exp = null;
        List<Map<String, String>> authorities = new ArrayList<>();
        try {
            payloadJson = new JSONObject(payload);
            username = payloadJson.getString("sub");
            exp = new Date(payloadJson.getLong("exp")*1000);
            JSONArray array = payloadJson.getJSONArray(AUTHORITIES);
            for (int i = 0; i < array.length(); i++) {
                Map<String, String> map = new HashMap<>();
                map.put(AUTHORITY,array.getJSONObject(i).getString(AUTHORITY));
                authorities.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            filterChain.doFilter(request, response);
            return;
        }

        User user = userService.loadUserByUsername(username);

        // Check if token in db is null or received token is different than this in db
        if (user.getToken() == null || !user.getToken().equals(String.valueOf(token.hashCode()))){
            //delete token in db and do not authorize user
            user.setToken(null);
            userService.saveUser(user);
            filterChain.doFilter(request, response);
            return;
        }

        // Check if token is expired (current token is the same as in db)
        if (exp.before(new Date())){
            // create new token, save to DB and send it to user
            Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                    .map(m -> new SimpleGrantedAuthority(m.get(AUTHORITY)))
                    .collect(Collectors.toSet());

            token = Jwts.builder()
                    .setSubject(username)
                    .claim(AUTHORITIES, simpleGrantedAuthorities)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis()+ Duration.ofMinutes(jwtConfig.getTokenExpirationAfterMinutes()).toMillis()))
                    .signWith(secretKey)
                    .compact();

            user.setToken(String.valueOf(token.hashCode()));
            response.addHeader(jwtConfig.getAuthorizationHeader(), jwtConfig.getTokenPrefix() + token);
        }

        // authenticate user
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            Claims body = claimsJws.getBody();
            String subject = body.getSubject();

            List<Map<String,String>> authoritiesList = (List<Map<String,String>>) body.get(AUTHORITIES);

            Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authoritiesList.stream()
                    .map(m -> new SimpleGrantedAuthority(m.get(AUTHORITY)))
                    .collect(Collectors.toSet());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    subject,
                    null,
                    simpleGrantedAuthorities
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException e){
            user.setToken(null);
            throw new IllegalStateException(String.format("Token %s cannot be trusted",token));
        }
        finally {
            userService.saveUser(user);
        }

        filterChain.doFilter(request, response);
    }
}
