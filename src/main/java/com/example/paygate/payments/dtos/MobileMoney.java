package com.example.paygate.payments.dtos;

import com.example.paygate.payments.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MobileMoney {
    @NotNull(message = "transaction type is required")
    private TransactionType transactionType;

    @NotBlank(message = "phone number is required")
    @Pattern(regexp = "^2547\\d{8}$", message = "phone number must start with 254 and be a valid Kenyan number")
    private String phoneNumber;
}
