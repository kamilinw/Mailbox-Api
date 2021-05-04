package pl.kamilwnek.mailbox.controller;

import com.google.common.net.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.kamilwnek.mailbox.dto.MailboxRequest;
import pl.kamilwnek.mailbox.model.Mailbox;
import pl.kamilwnek.mailbox.service.MailboxService;
import pl.kamilwnek.mailbox.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/mailbox")
public class MailboxController {

    private final MailboxService mailboxService;
    private final UserService userService;

    @Autowired
    public MailboxController(MailboxService mailboxService, UserService userService) {
        this.mailboxService = mailboxService;
        this.userService = userService;
    }


    @PatchMapping(value = "/{whichMailbox}")
    public Mailbox updateAll(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody MailboxRequest mailboxRequest,
            @PathVariable("whichMailbox") int whichMailbox){
        Long id = userService.getMailboxId(authorizationHeader,whichMailbox);
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

    @GetMapping("/temperature/{id}")
    public Double getTemperature(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                 @PathVariable("id") Long id){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return mailboxService.getTemperature(username, id);
    }

    @GetMapping("/pressure/{id}")
    public Double getPressure(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                              @PathVariable("id") Long id){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return mailboxService.getPressure(username, id);
    }

    @GetMapping("/humidity/{id}")
    public Double getHumidity(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                              @PathVariable("id") Long id){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return mailboxService.getHumidity(username, id);
    }

    @GetMapping("/battery/{id}")
    public Double getBattery(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                             @PathVariable("id") Long id){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return mailboxService.getBattery(username, id);
    }

    @GetMapping("/history/{id}")
    public List<LocalDateTime> getHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                          @PathVariable("id") Long id){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return mailboxService.getHistory(username, id);
    }

    @GetMapping("/new-mail/{id}")
    public boolean isNewMail(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                             @PathVariable("id") Long id){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return mailboxService.isNewMail(username, id);
    }

    @GetMapping("/notice/{id}")
    public boolean isNotice(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                            @PathVariable("id") Long id){
        String username = userService.getUsernameFromToken(authorizationHeader);
        return mailboxService.isNotice(username, id);
    }
}
