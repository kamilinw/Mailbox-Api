package pl.kamilwnek.mailbox.controller;

import com.google.common.net.HttpHeaders;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.kamilwnek.mailbox.dto.MailboxRequest;
import pl.kamilwnek.mailbox.model.Mailbox;
import pl.kamilwnek.mailbox.service.MailboxService;
import pl.kamilwnek.mailbox.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/mailbox")
public class MailboxController {

    private final MailboxService mailboxService;
    private final UserService userService;


    @PatchMapping(value = "/{whichMailbox}")
    public Mailbox updateAll(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody MailboxRequest mailboxRequest,
            @PathVariable("whichMailbox") int whichMailbox){
        String username = userService.getUsernameFromToken(authorizationHeader);
        Long id = userService.getMailboxId(username,whichMailbox);
        return mailboxService.updateAll(id, mailboxRequest);
    }


    @GetMapping()
    public Mailbox getFirstMailbox(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return mailboxService.getFirstMailbox(username);
    }

    @GetMapping("/{id}")
    public Mailbox getMailboxById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                  @PathVariable(value = "id") Long id){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return mailboxService.getMailboxById(username, id);
    }

    @GetMapping("/{id}/temperature")
    public Double getTemperature(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                 @PathVariable("id") Long id){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return mailboxService.getTemperature(username, id);
    }

    @GetMapping("/{id}/pressure")
    public Double getPressure(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                              @PathVariable("id") Long id){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return mailboxService.getPressure(username, id);
    }

    @GetMapping("/{id}/humidity")
    public Double getHumidity(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                              @PathVariable("id") Long id){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return mailboxService.getHumidity(username, id);
    }

    @GetMapping("/{id}/battery")
    public Double getBattery(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                             @PathVariable("id") Long id){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return mailboxService.getBattery(username, id);
    }

    @GetMapping("/{id}/history")
    public List<LocalDateTime> getHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                          @PathVariable("id") Long id){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return mailboxService.getHistory(username, id);
    }

    @GetMapping("/{id}/new-mail")
    public boolean isNewMail(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                             @PathVariable("id") Long id){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return mailboxService.isNewMail(username, id);
    }

    @GetMapping("/{id}/notice")
    public boolean isNotice(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                            @PathVariable("id") Long id){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return mailboxService.isNotice(username, id);
    }
}
