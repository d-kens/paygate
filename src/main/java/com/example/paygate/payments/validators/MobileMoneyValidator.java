package com.example.paygate.payments.validators;

import com.example.paygate.payments.dtos.PaymentRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MobileMoneyValidator implements ConstraintValidator<ValidMobileMoney, PaymentRequest> {
    @Override
    public boolean isValid(PaymentRequest request, ConstraintValidatorContext context) {
        if ("MPESA".equals(request.getProvider()))
            return request.getMobileMoney() != null;
        return true;
    }
}
