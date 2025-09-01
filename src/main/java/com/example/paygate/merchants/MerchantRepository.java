package com.example.paygate.merchants;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    boolean existsByUserId(Long userId);
    Optional<Merchant> findByApiKey(String apiKey);
}
