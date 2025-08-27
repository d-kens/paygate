package com.example.paygate.users.dtos;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String email;
    private String name;
}
