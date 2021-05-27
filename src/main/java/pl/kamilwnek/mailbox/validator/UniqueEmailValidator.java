package pl.kamilwnek.mailbox.validator;

import org.springframework.beans.factory.annotation.Autowired;
import pl.kamilwnek.mailbox.model.User;
import pl.kamilwnek.mailbox.service.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmailValue, String> {

    private final UserService userService;
    @Autowired
    public UniqueEmailValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        User user = userService.findUserByEmail(s);
        return user == null;
    }
}
