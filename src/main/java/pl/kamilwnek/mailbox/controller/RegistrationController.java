package pl.kamilwnek.mailbox.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.kamilwnek.mailbox.dto.RegistrationRequest;
import pl.kamilwnek.mailbox.service.RegistrationService;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping(path = "registration")
public class RegistrationController {

    private final RegistrationService registrationService;

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
