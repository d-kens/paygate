package com.example.paygate.payments.providers;

import com.example.paygate.merchants.Merchant;
import com.example.paygate.payments.dtos.PaymentRequest;
import org.springframework.stereotype.Service;

@Service
public class Mpesa implements PaymentProvider{
    @Override
    public String callback() {
        return "";
    }

    @Override
    public String authenticate() {
        return "";
    }

    @Override
    public String initiatePayment(Merchant merchant, PaymentRequest paymentRequest) {
        return "Payment Initiated Successfully";
    }

    @Override
    public String checkPaymentStatus() {
        return "";
    }
}
