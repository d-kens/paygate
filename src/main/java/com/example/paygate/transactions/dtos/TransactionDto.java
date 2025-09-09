package com.example.paygate.transactions.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDto {
    private Long id;
    private String currency;
    private String provider;
    private String status;
    private Long merchantId;
    private Long customerId;
    private BigDecimal amount;
    private String providerTransactionId;
    private String merchantPaymentReference;
}
