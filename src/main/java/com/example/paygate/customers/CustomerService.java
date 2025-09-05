package com.example.paygate.customers;

import com.example.paygate.customers.dtos.CreateCustomerDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomersRepository customersRepository;

    public Customer createCustomer(CreateCustomerDto customerDto) {
       return new Customer();
    }

}
