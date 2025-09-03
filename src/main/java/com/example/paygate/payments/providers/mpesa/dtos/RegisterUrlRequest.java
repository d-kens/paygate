package com.example.paygate.payments.providers.mpesa.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterUrlRequest {
    @JsonProperty("ShortCode")
    private String ShortCode;

    @JsonProperty("ResponseType")
    private String responseType;

    @JsonProperty("ConfirmationURL")
    private String confirmationURL;

    @JsonProperty("ValidationURL")
    private String validationURL;
}
