package com.example.paygate.merchants;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    boolean existsByUserId(Long userId);
    Optional<Merchant> findByApiKey(String apiKey);
}
