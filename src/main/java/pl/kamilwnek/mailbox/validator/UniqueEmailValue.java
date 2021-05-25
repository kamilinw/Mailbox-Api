package pl.kamilwnek.mailbox.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmailValue {
    String message() default "Podany adres e-mail już istnieje";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
