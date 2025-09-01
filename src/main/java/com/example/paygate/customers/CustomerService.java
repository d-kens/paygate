package com.example.paygate.customers;

import com.example.paygate.customers.dtos.CustomerDto;
import com.example.paygate.merchants.Merchant;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Customer createCustomer(CustomerDto customerDto, Merchant merchant) {
        Optional<Customer> existingCustomer = customerRepository.findByEmail(customerDto.getEmail());

        Customer customer;

        if (existingCustomer.isPresent()) {
            customer = existingCustomer.get();
            customer.setName(customerDto.getName());
            customer.setPhone(customerDto.getPhone());
        } else {
            customer = new Customer(
                    customerDto.getName(),
                    customerDto.getEmail(),
                    customerDto.getPhone()
            );
        }

        customer.getMerchants().add(merchant);
        return customerRepository.save(customer);
    }

}
