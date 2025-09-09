package com.example.paygate.transactions;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByMerchantId(Long merchantId);
    Optional<Transaction> findByProviderReferenceId(String providerReferenceId);
}
