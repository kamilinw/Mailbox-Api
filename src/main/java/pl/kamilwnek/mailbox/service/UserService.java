package pl.kamilwnek.mailbox.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import pl.kamilwnek.mailbox.dto.UserResponse;
import pl.kamilwnek.mailbox.exception.NoMailboxForThisUserException;
import pl.kamilwnek.mailbox.model.ConfirmationToken;
import pl.kamilwnek.mailbox.dto.CreateMailboxRequest;
import pl.kamilwnek.mailbox.model.Mailbox;
import pl.kamilwnek.mailbox.model.User;
import pl.kamilwnek.mailbox.repository.MailboxRepository;
import pl.kamilwnek.mailbox.repository.UserRepository;
import pl.kamilwnek.mailbox.security.ApplicationUserRole;
import pl.kamilwnek.mailbox.security.jwt.JwtConfig;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

import static java.util.Collections.emptyMap;

@Slf4j
@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private static final String USER_NOT_FOUND_MSG = "user with username %s not found";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final MailboxRepository mailboxRepository;
    private final JwtConfig jwtConfig;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
    }

    public User findUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public String signUpUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);

        var token = UUID.randomUUID().toString();

        var confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return token;
    }

    public int enableAppUser(String username) {
        return userRepository.enableAppUser(username);
    }

    public String createMailbox(CreateMailboxRequest request, String username) throws MethodArgumentNotValidException {

        User mailboxUser = userRepository.findByUsername(request.getUsername());

        // check if mailbox user already exists
        if (mailboxUser != null){

            // password for existing mailbox user check
            if (passwordEncoder.matches(request.getPassword(), mailboxUser.getPassword())){
                User user = userRepository.findByUsername(username);
                if (user != null){
                    List<Mailbox> mailboxList = user.getMailboxes();
                    User mailboxUserFinal = mailboxUser;

                    // check if user already have requested mailbox added to account
                    if (mailboxList.stream().anyMatch(m -> m.getMailboxId().equals(mailboxUserFinal.getMailboxes().get(0).getMailboxId()))){
                        ObjectError objectError = new ObjectError("username", "Ten użytkownik już posiada tą skrzynkę");
                        BindingResult bindingResult = new MapBindingResult(emptyMap(), "MyObject");
                        bindingResult.addError(objectError);
                        throw new MethodArgumentNotValidException(null, bindingResult);
                    }

                    user.addMailbox(mailboxUser.getMailboxes().get(0));
                    userRepository.save(user);
                    return "Added";
                }
            } else {
                ObjectError objectError = new ObjectError("password", "Podane hasło jest błędne");
                BindingResult bindingResult = new MapBindingResult(emptyMap(), "MyObject");
                bindingResult.addError(objectError);
                throw new MethodArgumentNotValidException(null, bindingResult);
            }
        }

        var mailbox = new Mailbox(request.getName());
        mailboxRepository.save(mailbox);

        mailboxUser = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                mailbox,
                ApplicationUserRole.MAILBOX);

        var currentUser = userRepository.findUserByUsername(username).orElseThrow(()->
                new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
        currentUser.addMailbox(mailbox);

        userRepository.save(mailboxUser);
        return "Created";
    }

    public String getUsernameFromToken(String authorizationHeader) {
        String token = authorizationHeader.replace(jwtConfig.getTokenPrefix(), "");
        String[] chunks = token.split("\\.");
        var decoder = Base64.getDecoder();

        var payload = new String(decoder.decode(chunks[1]));
        JSONObject payloadJson;
        var username = "";
        try {
            payloadJson = new JSONObject(payload);
            username = payloadJson.getString("sub");

        } catch (JSONException e) {
            e.printStackTrace();
            log.error("Can't get username from token. Probably wrong token");
        }

        if (username.isBlank())
            throw new UsernameNotFoundException(String.format("Username from token %s not found",token));

        return username;
    }

    public Long getMailboxId(String username, int whichMailbox) {
        var user = findUserByUsername(username);

        List<Mailbox> mailboxes = user.getMailboxes();

        if (whichMailbox >= mailboxes.size()){
            throw new NoMailboxForThisUserException("This user do not have this much mailboxes");
        }

        return mailboxes.get(whichMailbox).getMailboxId();
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null)
            return null;
        return new UserResponse(user.getUsername(),user.getEmail(),user.getMailboxes());
    }

    public UserResponse deleteMailbox(String username, Long id) {
        User user = userRepository.findByUsername(username);
        List<Mailbox> mailboxList = user.getMailboxes();
        mailboxList.removeIf(mailbox -> mailbox.getMailboxId().equals(id));
        user.setMailboxes(mailboxList);
        userRepository.saveAndFlush(user);


        Mailbox mailbox = mailboxRepository.findById(id).orElse(null);
        if (mailbox != null){
            long usersCount = mailbox.getUsers().size();
            if (usersCount == 1){
                User mailboxUser = mailbox.getUsers().get(0);

                mailboxRepository.delete(mailbox);
                mailboxRepository.flush();

                userRepository.deleteById(mailboxUser.getUserId());
                userRepository.flush();
            }
        }
        return new UserResponse(user.getUsername(), user.getEmail(), user.getMailboxes());
    }
}
