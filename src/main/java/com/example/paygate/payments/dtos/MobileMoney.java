package com.example.paygate.payments.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MobileMoney {
    @NotBlank(message = "phone number is required")
    @Pattern(regexp = "^2547\\d{8}$", message = "phone number must start with 254 and be a valid Kenyan number")
    private String phoneNumber;
}
