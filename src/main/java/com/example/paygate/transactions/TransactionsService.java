package com.example.paygate.transactions;

import com.example.paygate.customers.CustomerRepository;
import com.example.paygate.exceptions.NotFoundException;
import com.example.paygate.merchants.MerchantRepository;
import com.example.paygate.transactions.dtos.CreateTransactionRequest;
import com.example.paygate.transactions.dtos.TransactionDto;
import com.example.paygate.transactions.mappers.TransactionMapper;
import jakarta.transaction.TransactionalException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TransactionsService {
    private final TransactionMapper transactionMapper;
    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;
    private final TransactionsRepository transactionRepository;

    public TransactionDto createTransaction(CreateTransactionRequest transactionRequest) {
        var merchantId = transactionRequest.getMerchantId();
        var customerId = transactionRequest.getCustomerId();

        var transaction = transactionMapper.toEntity(transactionRequest);

        var customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) throw new NotFoundException("Customer with ID " + customerId + " not found");

        var merchant = merchantRepository.findById(transactionRequest.getMerchantId()).orElse(null);
        if (merchant == null) throw new NotFoundException("Merchant with ID " + merchantId + " not found");

        transaction.setCustomer(customer);
        transaction.setMerchant(merchant);

        transactionRepository.save(transaction);

        return transactionMapper.toDto(transaction);
    }

    public List<TransactionDto> findAllTransactions() {
        return transactionRepository.findAll().stream().map(transactionMapper::toDto).toList();
    }
    
    public List<TransactionDto> findTransactionsByMerchantId(Long merchantId) {
        return transactionRepository.findByMerchantId(merchantId).stream().map(transactionMapper::toDto).toList();
    }

    public TransactionDto findTransactionById(Long transactionId) {
        var transaction = transactionRepository.findById(transactionId).orElseThrow(
                () -> new NotFoundException("Transaction with ID " + transactionId + " not found")
        );
        return transactionMapper.toDto(transaction);
    }


}
