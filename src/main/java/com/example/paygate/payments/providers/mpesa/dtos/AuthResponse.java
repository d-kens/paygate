package com.example.paygate.payments.providers.mpesa.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthResponse {
    @JsonProperty("expires_in")
    private String expiresIn;

    @JsonProperty("access_token")
    private String accessToken;
}
