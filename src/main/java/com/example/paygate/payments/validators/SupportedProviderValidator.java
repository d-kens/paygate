package com.example.paygate.payments.validators;

import com.example.paygate.payments.enums.PaymentProviderType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class SupportedProviderValidator implements ConstraintValidator<SupportedProvider, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return Arrays.stream(PaymentProviderType.values())
                .anyMatch(p -> p.name().equals(value));
    }
}

