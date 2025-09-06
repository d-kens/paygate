package com.example.paygate.customers;

import com.example.paygate.customers.dtos.CreateCustomerDto;
import com.example.paygate.customers.dtos.CustomerDto;
import com.example.paygate.exceptions.NotFoundException;
import com.example.paygate.merchants.Merchant;
import com.example.paygate.merchants.MerchantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerMapper customerMapper;
    private final MerchantRepository merchantRepository;
    private final CustomersRepository customersRepository;

    public CustomerDto createCustomer(CreateCustomerDto customerDto) {
        Merchant merchant = merchantRepository.findById(customerDto.getMerchantId()).orElseThrow(
                () -> new NotFoundException("Merchant with ID " + customerDto.getMerchantId() + " not found")
        );

        Optional<Customer> existingCustomer = customersRepository.findByEmailAndMerchantId(
                customerDto.getEmail(), customerDto.getMerchantId()
        );

        Customer customer;

        if (existingCustomer.isPresent()) {
            customer = existingCustomer.get();
            customer.setName(customerDto.getName());
            customer.setPhone(customerDto.getPhone());
        } else {
            customer = customerMapper.toEntity(customerDto);
            customer.setMerchant(merchant);
        }

        customersRepository.save(customer);
        return customerMapper.toDto(customer);
    }

}
