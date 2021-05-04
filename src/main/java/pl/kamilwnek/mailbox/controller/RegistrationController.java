package pl.kamilwnek.mailbox.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.kamilwnek.mailbox.dto.RegistrationRequest;
import pl.kamilwnek.mailbox.service.RegistrationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public String register(@RequestBody @Valid RegistrationRequest request){
        try{
            return registrationService.register(request);
        } catch (IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Registration failure", e.getCause());
        }
    }

    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token){
        return registrationService.confirm(token);
    }
}
