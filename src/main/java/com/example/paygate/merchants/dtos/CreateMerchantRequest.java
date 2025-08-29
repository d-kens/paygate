package com.example.paygate.merchants.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class CreateMerchantRequest {
    @NotBlank(message = "merchant name is required")
    @Size(min = 4, max = 50, message = "merchant name must be between 4 to 50 characters")
    private String name;

    @Size(max = 500, message = "webhook url cannot exceed 500 characters")
    private String webhookUrl;
}
