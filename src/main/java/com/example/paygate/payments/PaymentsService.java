package com.example.paygate.payments;

import com.example.paygate.merchants.Merchant;
import com.example.paygate.payments.dtos.PaymentRequest;
import com.example.paygate.payments.enums.Providers;
import com.example.paygate.payments.providers.Mpesa;
import com.example.paygate.payments.providers.PaymentProvider;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentsService {

    private final Map<Providers, PaymentProvider> paymentProviders = new HashMap<>();

    public PaymentsService(Mpesa mpesa) {
        this.paymentProviders.put(Providers.MPESA, mpesa);
    }

    public String initiatePayment(Merchant merchant, PaymentRequest paymentRequest) {
        System.out.println(merchant);
        PaymentProvider paymentProvider = paymentProviders.get(Providers.valueOf(paymentRequest.getProvider()));
        return paymentProvider.initiatePayment(merchant, paymentRequest);
    }

}
