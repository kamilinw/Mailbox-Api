package pl.kamilwnek.mailbox.controller;

import com.google.common.net.HttpHeaders;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.kamilwnek.mailbox.dto.CreateMailboxRequest;
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
        return userService.getMailboxId(authorizationHeader, whichMailbox);
    }

    @GetMapping("/username")
    public String getUsername(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        return userService.getUsernameFromToken(authorizationHeader);
    }

    @PostMapping("/mailbox")
    public String createMailbox(
            @RequestBody @Valid CreateMailboxRequest createMailboxRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        return userService.createMailbox(createMailboxRequest,authorizationHeader);
    }

}
