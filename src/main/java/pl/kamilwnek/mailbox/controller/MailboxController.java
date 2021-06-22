package pl.kamilwnek.mailbox.controller;

import com.google.common.net.HttpHeaders;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.kamilwnek.mailbox.dto.MailboxRequest;
import pl.kamilwnek.mailbox.model.Mailbox;
import pl.kamilwnek.mailbox.service.MailboxService;
import pl.kamilwnek.mailbox.service.UserService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/skrzynka")
public class MailboxController {

    private final MailboxService mailboxService;
    private final UserService userService;


    @PutMapping(value = "/{whichMailbox}")
    public Mailbox updateAll(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody MailboxRequest mailboxRequest,
            @PathVariable("whichMailbox") int whichMailbox){
        String username = userService.getUsernameFromToken(authorizationHeader);
        Long id = userService.getMailboxId(username,whichMailbox);
        return mailboxService.updateAll(id, mailboxRequest);
    }
}
