package com.example.paygate.transactions.mappers;


import com.example.paygate.transactions.Transaction;
import com.example.paygate.transactions.dtos.CreateTransactionRequest;
import com.example.paygate.transactions.dtos.TransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    Transaction toEntity(CreateTransactionRequest transactionRequest);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "merchant.id", target = "merchantId")
    TransactionDto toDto(Transaction transaction);
}
