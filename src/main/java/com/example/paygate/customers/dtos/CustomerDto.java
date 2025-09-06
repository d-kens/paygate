package com.example.paygate.customers.dtos;


import lombok.Data;

@Data
public class CustomerDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Long merchantId;
}
