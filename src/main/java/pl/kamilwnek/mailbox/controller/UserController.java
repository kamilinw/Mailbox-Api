package pl.kamilwnek.mailbox.controller;

import com.google.common.net.HttpHeaders;
import jdk.jfr.ContentType;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import pl.kamilwnek.mailbox.dto.ChangeEmailRequest;
import pl.kamilwnek.mailbox.dto.ChangePasswordRequest;
import pl.kamilwnek.mailbox.dto.CreateMailboxRequest;
import pl.kamilwnek.mailbox.dto.UserResponse;
import pl.kamilwnek.mailbox.service.UserService;
import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/id")
    public Long getMailboxId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                             @RequestParam(value = "whichMailbox", required = false, defaultValue = "0") int whichMailbox){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return userService.getMailboxId(username, whichMailbox);
    }

    @GetMapping("/username")
    public String getUsername(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        return userService.getUsernameFromToken(authorizationHeader);
    }

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

    @PostMapping("/password")
    public UserResponse changePassword(
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws MethodArgumentNotValidException {
        String username = userService.getUsernameFromToken(authorizationHeader);
        return userService.changePassword(changePasswordRequest, username);
    }

    @PostMapping("/mailbox")
    public String createMailbox(
            @RequestBody CreateMailboxRequest createMailboxRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws MethodArgumentNotValidException {
        String username = userService.getUsernameFromToken(authorizationHeader);
        return userService.createMailbox(createMailboxRequest,username);
    }

    @DeleteMapping("/mailbox/{id}")
    public UserResponse deleteMailbox(
            @PathVariable("id") Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String username = userService.getUsernameFromToken(authorizationHeader);
        return userService.deleteMailbox(username, id);
    }

}
