package com.example.paygate.payments.providers.mpesa.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MpesaRegisterUrlResponse {
    @JsonProperty("OriginatorCoversationID")
    private String originatorCoversationID;

    @JsonProperty("ResponseCode")
    private String responseCode;

    @JsonProperty("ResponseDescription")
    private String responseDescription;
}
