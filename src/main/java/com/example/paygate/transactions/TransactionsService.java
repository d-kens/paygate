package com.example.paygate.transactions;

import com.example.paygate.customers.CustomersRepository;
import com.example.paygate.exceptions.NotFoundException;
import com.example.paygate.merchants.MerchantRepository;
import com.example.paygate.transactions.dtos.CreateTransactionDto;
import com.example.paygate.transactions.dtos.TransactionDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TransactionsService {
    private final TransactionMapper transactionMapper;
    private final MerchantRepository merchantRepository;
    private final CustomersRepository customersRepository;
    private final TransactionsRepository transactionRepository;

    public TransactionDto createTransaction(CreateTransactionDto transactionDto) {
        var merchantId = transactionDto.getMerchantId();
        var customerId = transactionDto.getCustomerId();

        var customer = customersRepository.findById(customerId).orElseThrow(
                () -> new NotFoundException(
                        "Customer with ID " + customerId + " not found"
                )
        );
        var merchant = merchantRepository.findById(merchantId).orElseThrow(
                () -> new NotFoundException(
                        "Merchant with ID " + merchantId + " not found"
                )
        );

        var transaction = transactionMapper.toEntity(transactionDto);

        transaction.setCustomer(customer);
        transaction.setMerchant(merchant);

        transactionRepository.save(transaction);

        return transactionMapper.toDto(transaction);
    }

    public List<TransactionDto> findAllTransactions() {
        return transactionRepository.findAll().stream().map(transactionMapper::toDto).toList();
    }

    public TransactionDto findTransactionById(Long transactionId) {
        var transaction = transactionRepository.findById(transactionId).orElseThrow(
                () -> new NotFoundException("Transaction with ID " + transactionId + " not found")
        );
        return transactionMapper.toDto(transaction);
    }

    public List<TransactionDto> findTransactionsByMerchantId(Long merchantId) {
        merchantRepository.findById(merchantId).orElseThrow(
                () -> new NotFoundException("Merchant with ID " + merchantId + " not found")
        );

        return transactionRepository.findByMerchantId(merchantId).stream().map(transactionMapper::toDto).toList();
    }

    public Transaction updateTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
