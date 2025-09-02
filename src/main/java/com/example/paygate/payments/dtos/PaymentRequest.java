package com.example.paygate.payments.dtos;

import com.example.paygate.payments.validators.SupportedProvider;
import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.example.paygate.payments.validators.ValidMobileMoney;

import java.math.BigDecimal;


@Data
@ValidMobileMoney
public class PaymentRequest {
    @NotBlank(message = "customer name is required")
    private String name;

    @Email(message = "email must be a valid email")
    @NotBlank(message = "customer email address is required")
    private String emailAddress;

    @NotBlank(message = "provider is required")
    @SupportedProvider
    private String provider;

    @NotNull(message = "amount is required")
    @Positive(message = "amount should be a positive value")
    private BigDecimal amount;

    @NotBlank(message = "payment reference is required")
    private String paymentRef;

    @Valid
    private MobileMoney mobileMoney;
}
