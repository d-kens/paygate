package com.example.paygate.customers;

import com.example.paygate.customers.dtos.CreateCustomerRequest;
import com.example.paygate.customers.dtos.CustomerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toEntity(CreateCustomerRequest createCustomerRequest);

    @Mapping(source = "merchant.id", target = "merchantId")
    CustomerDto toDto(Customer customer);
}
