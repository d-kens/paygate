package com.example.paygate.merchants;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    boolean existsByUserId(Long userId);
}
