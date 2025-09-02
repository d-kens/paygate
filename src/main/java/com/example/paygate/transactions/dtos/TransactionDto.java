package com.example.paygate.transactions.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDto {
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
