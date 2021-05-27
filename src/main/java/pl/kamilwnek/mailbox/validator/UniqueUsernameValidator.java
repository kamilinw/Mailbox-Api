package pl.kamilwnek.mailbox.validator;

import org.springframework.beans.factory.annotation.Autowired;
import pl.kamilwnek.mailbox.model.User;
import pl.kamilwnek.mailbox.service.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsernameValue, String> {

    private final UserService userService;
    @Autowired
    public UniqueUsernameValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        User user = userService.findUserByUsername(s);

        return user == null;
    }
}
