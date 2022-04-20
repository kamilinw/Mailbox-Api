package pl.kamilwnek.mailbox.controller;

import com.google.common.net.HttpHeaders;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import pl.kamilwnek.mailbox.dto.*;
import pl.kamilwnek.mailbox.model.Mailbox;
import pl.kamilwnek.mailbox.service.UserService;
import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/api/uzytkownik")
public class UserController {

    private final UserService userService;

    @GetMapping
    public UserResponse getUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return userService.getUserByUsername(username);
    }

    @PostMapping("/email")
    public UserResponse changeEmail(
            @RequestBody @Valid ChangeEmailRequest changeEmailRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws MethodArgumentNotValidException {
        String username = userService.getUsernameFromToken(authorizationHeader);
        return userService.changeEmail(changeEmailRequest, username);
    }

    @PostMapping("/haslo")
    public UserResponse changePassword(
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws MethodArgumentNotValidException {
        String username = userService.getUsernameFromToken(authorizationHeader);
        return userService.changePassword(changePasswordRequest, username);
    }

    @PostMapping("/skrzynka")
    public String createMailbox(
            @RequestBody CreateMailboxRequest createMailboxRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws MethodArgumentNotValidException {
        String username = userService.getUsernameFromToken(authorizationHeader);
        return userService.createMailbox(createMailboxRequest,username);
    }

    @DeleteMapping("/skrzynka/{id}")
    public UserResponse deleteMailbox(
            @PathVariable("id") Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String username = userService.getUsernameFromToken(authorizationHeader);
        return userService.deleteMailbox(username, id);
    }

    @PostMapping("/skrzynka/{id}/subscribeemail")
    public Mailbox subscribeEmail(@RequestBody @Valid SubscribeEmailRequest subscribeEmailRequest,
                                  @PathVariable("id") Long id){
        return userService.subscribeEmail(id, subscribeEmailRequest);
    }

    @DeleteMapping("/skrzynka/{id}/subscribeemail")
    public Mailbox unsubscribeEmail(@RequestBody @Valid SubscribeEmailRequest subscribeEmailRequest,
                                  @PathVariable("id") Long id){
        return userService.unsubscribeEmail(id, subscribeEmailRequest);
    }
}
