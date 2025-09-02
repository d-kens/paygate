package com.example.paygate.transactions.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CreateTransactionRequest {

    @NotBlank(message = "Transaction status is required")
    private String status;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Merchant ID is required")
    private Long merchantId;

    @NotBlank(message = "Provider is required")
    private String provider;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Payment reference is required")
    private String paymentReference;
}
