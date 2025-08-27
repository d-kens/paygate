package com.example.paygate.users.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "old password is required")
    @Size(min = 6, max = 25, message = "old password  must be between 6 to 25 characters")
    private String oldPassword;

    @NotBlank(message = "new password is required")
    @Size(min = 6, max = 25, message = "new password  must be between 6 to 25 characters")
    private String newPassword;
}
