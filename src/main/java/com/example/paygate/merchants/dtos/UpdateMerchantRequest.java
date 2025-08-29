package com.example.paygate.merchants.dtos;

import lombok.Data;

@Data
public class UpdateMerchantRequest {
    private String name;
    private String webhookUrl;
    private Boolean webhookActive;
}
