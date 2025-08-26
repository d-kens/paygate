package com.example.paygate.exceptions.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorDto {
    private String error;
}
