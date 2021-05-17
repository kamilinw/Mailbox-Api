package pl.kamilwnek.mailbox.security;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.kamilwnek.mailbox.security.jwt.JwtConfig;
import pl.kamilwnek.mailbox.security.jwt.JwtTokenVerifier;
import pl.kamilwnek.mailbox.security.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import pl.kamilwnek.mailbox.service.UserService;


import javax.crypto.SecretKey;

import static pl.kamilwnek.mailbox.security.ApplicationUserPermission.*;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String USER_PATTERN = "/api/user/**";
    private static final String MAILBOX_PATTERN = "/api/mailbox/**";
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(),jwtConfig,secretKey, userService))
                .addFilterAfter(new JwtTokenVerifier(jwtConfig,secretKey, userService), JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                    .antMatchers("/","index","/css/*","/js/*").permitAll()
                    .antMatchers("/registration/**").permitAll()
                    .antMatchers(HttpMethod.DELETE,USER_PATTERN).hasAuthority(USER_WRITE.getPermission())
                    .antMatchers(HttpMethod.POST,USER_PATTERN).hasAuthority(USER_WRITE.getPermission())
                    .antMatchers(HttpMethod.PUT,USER_PATTERN).hasAuthority(USER_WRITE.getPermission())
                    .antMatchers(HttpMethod.PATCH,USER_PATTERN).hasAuthority(USER_WRITE.getPermission())
                    .antMatchers(HttpMethod.GET,USER_PATTERN).hasAuthority(USER_READ.getPermission())

                    .antMatchers(HttpMethod.DELETE,MAILBOX_PATTERN).hasAuthority(MAILBOX_WRITE.getPermission())
                    .antMatchers(HttpMethod.POST,MAILBOX_PATTERN).hasAuthority(MAILBOX_WRITE.getPermission())
                    .antMatchers(HttpMethod.PUT,MAILBOX_PATTERN).hasAuthority(MAILBOX_WRITE.getPermission())
                    .antMatchers(HttpMethod.PATCH,MAILBOX_PATTERN).hasAuthority(MAILBOX_WRITE.getPermission())
                    .antMatchers(HttpMethod.GET,MAILBOX_PATTERN).hasAuthority(MAILBOX_READ.getPermission())
                    .antMatchers("/**").permitAll()
                    .anyRequest().authenticated();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        var provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userService);

        return provider;
    }


}
