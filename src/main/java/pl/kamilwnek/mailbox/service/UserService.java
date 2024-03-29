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
import pl.kamilwnek.mailbox.dto.*;
import pl.kamilwnek.mailbox.exception.IdNotFoundException;
import pl.kamilwnek.mailbox.exception.NoMailboxForThisUserException;
import pl.kamilwnek.mailbox.model.ConfirmationToken;
import pl.kamilwnek.mailbox.model.Mailbox;
import pl.kamilwnek.mailbox.model.SubscribeEmail;
import pl.kamilwnek.mailbox.model.User;
import pl.kamilwnek.mailbox.repository.MailboxRepository;
import pl.kamilwnek.mailbox.repository.SubscribeEmailRepository;
import pl.kamilwnek.mailbox.repository.UserRepository;
import pl.kamilwnek.mailbox.security.ApplicationUserRole;
import pl.kamilwnek.mailbox.security.jwt.JwtConfig;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Collections.emptyMap;

@Slf4j
@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private static final String USER_NOT_FOUND_MSG = "user with username %s not found";
    private final UserRepository userRepository;
    private final SubscribeEmailRepository subscribeEmailRepository;
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

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
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
                    Set<Mailbox> mailboxList = user.getMailboxes();
                    User mailboxUserFinal = mailboxUser;

                    // check if user already have requested mailbox added to account
                    if (mailboxList.stream().anyMatch(m -> m.getMailboxId().equals(mailboxUserFinal.getMailboxes().stream().findFirst().get().getMailboxId()))){
                        ObjectError objectError = new ObjectError("username", "Ten użytkownik już posiada tą skrzynkę");
                        BindingResult bindingResult = new MapBindingResult(emptyMap(), "MyObject");
                        bindingResult.addError(objectError);
                        throw new MethodArgumentNotValidException(null, bindingResult);
                    }

                    Optional<Mailbox> mailbox = mailboxUser.getMailboxes().stream().findFirst();
                    if (mailbox.isPresent()){
                        Set<Mailbox> mailboxes = user.getMailboxes();
                        mailboxes.add(mailbox.get());
                        user.setMailboxes(mailboxes);
                        userRepository.save(user);
                        return "Added";
                    } else {
                        ObjectError objectError = new ObjectError("mailbox", "Nie znaleziono skrzynki");
                        BindingResult bindingResult = new MapBindingResult(emptyMap(), "MyObject");
                        bindingResult.addError(objectError);
                        throw new MethodArgumentNotValidException(null, bindingResult);
                    }

                }
            } else {
                ObjectError objectError = new ObjectError("password", "Podane hasło jest błędne");
                BindingResult bindingResult = new MapBindingResult(emptyMap(), "MyObject");
                bindingResult.addError(objectError);
                throw new MethodArgumentNotValidException(null, bindingResult);
            }
        }

        Mailbox mailbox = new Mailbox(request.getName());
        mailboxRepository.save(mailbox);

        mailboxUser = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                mailbox,
                ApplicationUserRole.MAILBOX);

        User currentUser = userRepository.findUserByUsername(username).orElseThrow(()->
                new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
        currentUser.addMailbox(mailbox);

        userRepository.save(mailboxUser);
        return "Created";
    }

    public String getUsernameFromToken(String authorizationHeader) {
        String token = authorizationHeader.replace(jwtConfig.getTokenPrefix(), "");
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();

        String payload = new String(decoder.decode(chunks[1]));
        JSONObject payloadJson;
        String username = "";
        try {
            payloadJson = new JSONObject(payload);
            username = payloadJson.getString("sub");

        } catch (JSONException e) {
            e.printStackTrace();
            log.error("Can't get username from token. Probably wrong token");
        }

        if (username.isEmpty())
            throw new UsernameNotFoundException(String.format("Username from token %s not found",token));

        return username;
    }

    public Long getMailboxId(String username, int whichMailbox) {
        User user = findUserByUsername(username);

        Set<Mailbox> mailboxes = user.getMailboxes();

        if (whichMailbox >= mailboxes.size()){
            throw new NoMailboxForThisUserException("This user do not have this much mailboxes");
        }

        return mailboxes.stream().findFirst().get().getMailboxId();
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
        Set<Mailbox> mailboxList = user.getMailboxes();
        mailboxList.removeIf(mailbox -> mailbox.getMailboxId().equals(id));
        user.setMailboxes(mailboxList);
        userRepository.saveAndFlush(user);


        Mailbox mailbox = mailboxRepository.findById(id).orElse(null);
        if (mailbox != null){
            long usersCount = mailbox.getUsers().size();
            if (usersCount == 1){
                User mailboxUser = mailbox.getUsers().stream().findFirst().get();

                mailboxRepository.delete(mailbox);
                mailboxRepository.flush();

                userRepository.deleteById(mailboxUser.getUserId());
                userRepository.flush();
            }
        }
        return new UserResponse(user.getUsername(), user.getEmail(), user.getMailboxes());
    }

    public UserResponse changeEmail(ChangeEmailRequest changeEmailRequest, String username) throws MethodArgumentNotValidException {
        User user = userRepository.findByUsername(username);
        if (user.getEmail().equals(changeEmailRequest.getOldEmail())){
            user.setEmail(changeEmailRequest.getNewEmail());
            userRepository.save(user);
            return new UserResponse(user.getUsername(), user.getEmail(), user.getMailboxes());
        } else {
            ObjectError objectError = new ObjectError("oldEmail", "Podany email jest inny niż aktualny email użytkownika");
            BindingResult bindingResult = new MapBindingResult(emptyMap(), "MyObject");
            bindingResult.addError(objectError);
            throw new MethodArgumentNotValidException(null, bindingResult);
        }

    }

    public UserResponse changePassword(ChangePasswordRequest changePasswordRequest, String username) throws MethodArgumentNotValidException {
        User user = userRepository.findByUsername(username);
        if (passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())){
            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            userRepository.save(user);
            return new UserResponse(user.getUsername(), user.getEmail(), user.getMailboxes());
        } else {
            ObjectError objectError = new ObjectError("oldPassword", "Podane hasło jest inne niż aktualne hasło użytkownika");
            BindingResult bindingResult = new MapBindingResult(emptyMap(), "MyObject");
            bindingResult.addError(objectError);
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
    }

    public Mailbox subscribeEmail(Long id, SubscribeEmailRequest subscribeEmailRequest) {
        Mailbox mailbox = mailboxRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Mailbox with this id not found"));
        SubscribeEmail subscribeEmail = subscribeEmailRepository.findByEmail(subscribeEmailRequest.getEmail()).orElse(new SubscribeEmail());
        subscribeEmail.setEmail(subscribeEmailRequest.getEmail());
        subscribeEmailRepository.save(subscribeEmail);
        mailbox.addEmail(subscribeEmail);
        return mailboxRepository.save(mailbox);
    }

    public Mailbox unsubscribeEmail(Long id, SubscribeEmailRequest subscribeEmailRequest) {
        Mailbox mailbox = mailboxRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Mailbox with this id not found"));
        SubscribeEmail subscribeEmail = subscribeEmailRepository
                .findByEmail(subscribeEmailRequest.getEmail())
                .orElseThrow(() -> new IdNotFoundException("Subscribe email with this id not found"));

        mailbox.removeEmail(subscribeEmail);
        return mailboxRepository.save(mailbox);
    }
}
