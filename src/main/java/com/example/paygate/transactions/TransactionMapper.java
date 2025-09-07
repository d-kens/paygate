package com.example.paygate.transactions;


import com.example.paygate.transactions.dtos.CreateTransactionDto;
import com.example.paygate.transactions.dtos.TransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    Transaction toEntity(CreateTransactionDto createTransactionDto);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "merchant.id", target = "merchantId")
    TransactionDto toDto(Transaction transaction);
}
