package com.example.paygate.payments.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SupportedProviderValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportedProvider {
    String message() default "Provider is not supported";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
