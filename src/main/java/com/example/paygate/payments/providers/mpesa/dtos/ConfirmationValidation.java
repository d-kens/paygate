package com.example.paygate.payments.providers.mpesa.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfirmationValidation {
    @JsonProperty("ResultCode")
    private String resultCode;

    @JsonProperty("ResultDesc")
    private String resultDesc;
}
