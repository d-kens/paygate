package com.example.paygate.payments.validators;

import com.example.paygate.payments.enums.TransactionType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class SupportedTransactionTypeValidator implements ConstraintValidator<SupportedTransactionType, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return Arrays.stream(TransactionType.values())
                .anyMatch(p -> p.name().equals(value));
    }
}
