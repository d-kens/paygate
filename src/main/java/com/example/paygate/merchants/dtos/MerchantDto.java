package com.example.paygate.merchants.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MerchantDto {
    private Long id;
    private String name;
    private String apiKey;
    private Long user_id;
}
