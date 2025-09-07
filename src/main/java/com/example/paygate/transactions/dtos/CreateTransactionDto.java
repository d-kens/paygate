package com.example.paygate.transactions.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CreateTransactionDto {
    private Long merchantId;
    private Long customerId;
    private BigDecimal amount;
    private String provider;
    private String merchantPaymentReference;
    private String providerReferenceId;
}
