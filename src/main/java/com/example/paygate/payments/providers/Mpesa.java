package com.example.paygate.payments.providers;

import com.example.paygate.customers.Customer;
import com.example.paygate.customers.CustomerService;
import com.example.paygate.customers.dtos.CustomerDto;
import com.example.paygate.merchants.Merchant;
import com.example.paygate.payments.configs.MpesaConfig;
import com.example.paygate.payments.dtos.PaymentDto;
import com.example.paygate.payments.dtos.PaymentRequest;
import com.example.paygate.payments.dtos.mpesa.MpesaAuthResponse;
import com.example.paygate.payments.enums.TransactionType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@AllArgsConstructor
public class Mpesa implements PaymentProvider {

    private final MpesaConfig mpesaConfig;
    private final CustomerService customerService;

    @Override
    public String callback() {
        return "";
    }

    @Override
    public String authenticate() {
        RestClient restClient = RestClient.builder().baseUrl(mpesaConfig.getAuthUrl()).build();
        var authResponse = restClient
                .get()
                .headers(headers -> headers.setBasicAuth(
                        mpesaConfig.getConsumerKey(),
                        mpesaConfig.getConsumerSecret()
                ))
                .retrieve()
                .body(MpesaAuthResponse.class);

        return authResponse.getAccess_token();
    }

    @Override
    public PaymentDto initiatePayment(PaymentRequest paymentRequest, Merchant merchant) {
        var customer = buildCustomerAccount(paymentRequest, merchant);

        if (paymentRequest.getMobileMoney().getTransactionType() == TransactionType.STK) {
            return initiateStkPayment(paymentRequest, merchant, customer);
        } else {
            return initiatePayBillPayment(paymentRequest, merchant, customer);
        }
    }

    @Override
    public String checkPaymentStatus() {
        return "";
    }


    private PaymentDto initiateStkPayment(PaymentRequest paymentRequest, Merchant merchant, Customer customer) {
        var authToken = this.authenticate();
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println(authToken);
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        return new PaymentDto();
    }

    private PaymentDto initiatePayBillPayment(PaymentRequest paymentRequest, Merchant merchant, Customer customer) {
        return new PaymentDto();
    }


    public Customer buildCustomerAccount(PaymentRequest paymentRequest, Merchant merchant) {
        CustomerDto customerDto = CustomerDto.builder()
                .name(paymentRequest.getName())
                .email(paymentRequest.getEmailAddress())
                .phone(paymentRequest.getMobileMoney().getPhoneNumber())
                .build();

        return customerService.createCustomer(customerDto, merchant);
    }
}
