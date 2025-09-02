package com.example.paygate.payments.dtos.mpesa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MpesaAuthResponse {
    @JsonProperty("expires_in")
    private String expiresIn;

    @JsonProperty("access_token")
    private String accessToken;
}
