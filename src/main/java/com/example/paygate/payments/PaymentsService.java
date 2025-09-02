package com.example.paygate.payments;

import com.example.paygate.merchants.Merchant;
import com.example.paygate.transactions.dtos.TransactionDto;
import com.example.paygate.payments.dtos.PaymentRequest;
import com.example.paygate.payments.enums.Providers;
import com.example.paygate.payments.providers.mpesa.Mpesa;
import com.example.paygate.payments.providers.mpesa.PaymentProvider;

import com.example.paygate.transactions.TransactionService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentsService {

    private final TransactionService transactionService;
    private final Map<Providers, PaymentProvider> paymentProviders = new HashMap<>();

    public PaymentsService(
            Mpesa mpesa,
            TransactionService transactionService
    ) {
        this.paymentProviders.put(Providers.MPESA, mpesa);
        this.transactionService = transactionService;
    }

    public TransactionDto initiatePayment(PaymentRequest paymentRequest, Merchant merchant) {
        System.out.println(merchant);
        PaymentProvider paymentProvider = paymentProviders.get(Providers.valueOf(paymentRequest.getProvider()));
        return paymentProvider.initiatePayment(paymentRequest, merchant);
    }
}
