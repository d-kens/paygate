package com.example.paygate.payments.providers.mpesa.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MpesaResponse {

    @JsonProperty("Body")
    private Body body;

    @Data
    public static class Body {
        @JsonProperty("stkCallback")
        private StkCallBack stkCallBack;
    }

    @Data
    public static class StkCallBack {
        @JsonProperty("MerchantRequestID")
        private String merchantRequestID;

        @JsonProperty("CheckoutRequestID")
        private String checkoutRequestID;

        @JsonProperty("ResultCode")
        private int resultCode;

        @JsonProperty("ResultDesc")
        private String resultDesc;

        @JsonProperty("CallbackMetadata")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private CallBackMetaData callBackMetaData;
    }


    @Data
    public static class CallBackMetaData {
        @JsonProperty("Item")
        private Item[] item;
    }

    @Data
    public static class Item {
        @JsonProperty("Name")
        private String name;

        @JsonProperty("Value")
        private String value;
    }
}
