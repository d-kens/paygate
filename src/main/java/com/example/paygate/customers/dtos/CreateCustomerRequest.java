package com.example.paygate.customers.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateCustomerRequest {
    @NotBlank(message = "customer name is required")
    private String name;

    @Email(message = "email must be a valid email")
    @NotBlank(message = "customer email address is required")
    private String email;

    @NotNull(message = "merchant id must be provided")
    private Long merchantId;

    private String phone;
}
