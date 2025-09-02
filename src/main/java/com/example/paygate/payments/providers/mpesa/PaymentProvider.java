package com.example.paygate.payments.providers.mpesa;

import com.example.paygate.merchants.Merchant;
import com.example.paygate.transactions.dtos.TransactionDto;
import com.example.paygate.payments.dtos.PaymentRequest;

public interface PaymentProvider {
    String callback();
    String authenticate();
    TransactionDto initiatePayment(PaymentRequest paymentRequest, Merchant merchant);
    String checkPaymentStatus();
}
