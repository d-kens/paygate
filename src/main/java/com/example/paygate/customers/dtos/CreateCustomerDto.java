package com.example.paygate.customers.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateCustomerDto {
    @NotBlank(message = "customer name is required")
    private String name;

    @Email(message = "email must be a valid email")
    @NotBlank(message = "customer email address is required")
    private String email;

    private String phone;
}
