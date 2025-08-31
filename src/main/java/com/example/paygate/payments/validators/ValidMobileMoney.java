package com.example.paygate.payments.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MobileMoneyValidator.class)
public @interface ValidMobileMoney {
    String message() default "Mobile money information is required for mobile money payments";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
