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
    @Pattern(regexp = "^(\\+254|0)7\\d{8}$", message = "phone number must be a valid number")
    private String phoneNumber;
}
