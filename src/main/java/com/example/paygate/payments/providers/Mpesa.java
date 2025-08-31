package com.example.paygate.payments.providers;

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
    public String initiatePayment(PaymentRequest paymentRequest) {
        return "Payment Initiated Successfully";
    }

    @Override
    public String checkPaymentStatus() {
        return "";
    }
}
