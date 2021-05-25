package pl.kamilwnek.mailbox.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotStartWithMailboxValidator.class)
public @interface NotStartWithMailbox {
    String message() default "Nazwa użytkownika nie może zaczynać się od mailbox";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
