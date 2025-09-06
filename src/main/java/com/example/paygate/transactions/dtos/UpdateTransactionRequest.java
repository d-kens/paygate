package com.example.paygate.transactions.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UpdateTransactionRequest {
    private Long merchantId;
    private Long customerId;
    private BigDecimal amount;
    private String currency;
    private String provider;
    private String providerTransactionId;
    private String status;
    private String paymentReference;
    private String description;
}
