package com.example.paygate.payments;

import com.example.paygate.merchants.Merchant;
import com.example.paygate.payments.dtos.PaymentRequest;
import com.example.paygate.transactions.dtos.TransactionDto;
import com.example.paygate.payments.enums.PaymentProviderType;
import com.example.paygate.payments.providers.PaymentProvider;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentsService {
    private final Map<PaymentProviderType, PaymentProvider<?>> paymentProviders = new HashMap<>();

    public PaymentsService(List<PaymentProvider<?>> providers) {
        for (PaymentProvider<?> provider : providers) {
            paymentProviders.put(provider.getProviderType(), provider);
        }
    }

    public TransactionDto initiatePayment(PaymentRequest paymentRequest, Merchant merchant) {
        PaymentProviderType providerType = PaymentProviderType.valueOf(paymentRequest.getProvider());
        PaymentProvider<?> paymentProvider = paymentProviders.get(providerType);
        return paymentProvider.initiatePayment(paymentRequest, merchant);
    }
}
