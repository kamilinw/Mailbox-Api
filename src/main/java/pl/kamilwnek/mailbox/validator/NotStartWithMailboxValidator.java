package pl.kamilwnek.mailbox.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotStartWithMailboxValidator implements ConstraintValidator<NotStartWithMailbox, String> {


    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !s.startsWith("mailbox");
    }
}
