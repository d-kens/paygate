package com.example.paygate.users.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 25, message = "Password must be between 6 to 25 characters")
    private String password;

    @NotBlank(message = "name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;
}
