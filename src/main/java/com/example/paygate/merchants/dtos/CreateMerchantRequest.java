package com.example.paygate.merchants.dtos;

import lombok.Data;

import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import jakarta.validation.constraints.NotBlank;


@Data
public class CreateMerchantRequest {
    @NotBlank(message = "merchant name is required")
    @Size(min = 4, max = 50, message = "merchant name must be between 4 to 50 characters")
    private String name;

    @NotBlank(message = "webhook url is required")
    @URL(message = "webhook url must be a valid URL")
    @Size(max = 500, message = "webhook url cannot exceed 500 characters")
    private String webhookUrl;
}
