package com.example.paygate.customers;

import com.example.paygate.customers.dtos.CreateCustomerRequest;
import com.example.paygate.customers.dtos.CustomerDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/customers")
public class CustomersController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        var customerDto = customerService.createCustomer(request);
        var uri = uriComponentsBuilder.path("/customer/{customerId}").buildAndExpand(customerDto.getId()).toUri();
        return ResponseEntity.created(uri).body(customerDto);
    }

    @GetMapping
    public List<CustomerDto> getAllCustomers() {
        return customerService.findAllCustomers();
    }

    @GetMapping("/{customerId}")
    public CustomerDto getCustomer(@PathVariable Long customerId) {
        return customerService.findCustomerById(customerId);
    }
}
