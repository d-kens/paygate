package com.example.paygate.webhook.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WebhookPayload {
    private Long transactionId;
    private String status;
    private BigDecimal amount;
    private String currency;
    private String merchantPaymentReference;
}
