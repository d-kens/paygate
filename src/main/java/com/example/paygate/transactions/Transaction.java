package com.example.paygate.transactions;


import com.example.paygate.customers.Customer;
import com.example.paygate.merchants.Merchant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private BigDecimal amount;

    private String currency = "KES";

    private String provider;

    @Column(name = "provider_reference_id")
    private String providerReferenceId;

    @Column(name = "provider_transaction_id")
    private String providerTransactionId;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "payment_reference")
    private String paymentReference;

    private String description;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
