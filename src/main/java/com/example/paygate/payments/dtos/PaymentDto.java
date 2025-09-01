package com.example.paygate.payments.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentDto {
    private Long id;
    private Long merchantId;
    private Long customerId;
    private BigDecimal amount;
    private String currency;
    private String provider;
    private String providerTransactionId;
    private String status;
    private String paymentReference;
}
