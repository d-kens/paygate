package com.example.paygate.payments.dtos.mpesa;

import lombok.Data;

@Data
public class MpesaAuthResponse {
    private String expires_in;
    private String access_token;
}
