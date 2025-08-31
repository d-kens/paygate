package com.example.paygate.payments.providers;

import com.example.paygate.merchants.Merchant;
import com.example.paygate.payments.dtos.PaymentRequest;

public interface PaymentProvider {
    String callback();
    String authenticate();
    String initiatePayment(Merchant merchant, PaymentRequest paymentRequest);
    String checkPaymentStatus();
}
