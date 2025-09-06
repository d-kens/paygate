package com.example.paygate.customers;

import com.example.paygate.customers.dtos.CreateCustomerDto;
import com.example.paygate.customers.dtos.CustomerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toEntity(CreateCustomerDto customerDto);

    @Mapping(source = "merchant.id", target = "merchantId")
    CustomerDto toDto(Customer customer);
}
