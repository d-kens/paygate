package com.example.paygate.transactions;


import com.example.paygate.transactions.dtos.TransactionDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/transactions")
public class TransactionsController {

    private final TransactionsService transactionsService;

    @GetMapping
    public List<TransactionDto> findAllTransactions() {
        return transactionsService.findAllTransactions();
    }

    @GetMapping("/{transactionId}")
    public TransactionDto findTransactionById(@PathVariable Long transactionId) {
        return transactionsService.findTransactionById(transactionId);
    }
}
