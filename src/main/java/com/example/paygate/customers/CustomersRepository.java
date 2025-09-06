package com.example.paygate.customers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomersRepository extends JpaRepository<Customer, Long> {
   Optional<Customer> findByEmailAndMerchantId(String email, Long merchantId);
   List<Customer> findByMerchantId(Long merchantId);
}
