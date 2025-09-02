package com.example.paygate.payments.providers;

import com.example.paygate.customers.Customer;
import com.example.paygate.customers.CustomerService;
import com.example.paygate.customers.dtos.CreateCustomerDto;
import com.example.paygate.merchants.Merchant;
import com.example.paygate.payments.configs.MpesaConfig;
import com.example.paygate.payments.dtos.mpesa.MpesaStkRequest;
import com.example.paygate.payments.dtos.mpesa.MpesaStkResponse;
import com.example.paygate.transactions.TransactionService;
import com.example.paygate.transactions.dtos.CreateTransactionRequest;
import com.example.paygate.transactions.dtos.TransactionDto;
import com.example.paygate.payments.dtos.PaymentRequest;
import com.example.paygate.payments.dtos.mpesa.MpesaAuthResponse;
import com.example.paygate.payments.enums.TransactionType;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@Service
@AllArgsConstructor
public class Mpesa implements PaymentProvider {
    private final MpesaConfig mpesaConfig;
    private final CustomerService customerService;
    private final TransactionService transactionService;

    @Override
    public String callback() {
        return "";
    }

    @Override
    public String authenticate() {
        RestClient restClient = RestClient.builder()
                .baseUrl(mpesaConfig.getAuthUrl())
                .build();

        var authResponse = restClient
                .get()
                .uri(uriBuilder -> uriBuilder.queryParam("grant_type", "client_credentials").build())
                .headers(headers -> headers.setBasicAuth(
                        mpesaConfig.getConsumerKey(),
                        mpesaConfig.getConsumerSecret()
                ))
                .retrieve()
                .body(MpesaAuthResponse.class);

        return authResponse.getAccessToken();
    }

    @Override
    public TransactionDto initiatePayment(PaymentRequest paymentRequest, Merchant merchant) {
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


    private TransactionDto initiateStkPayment(PaymentRequest paymentRequest, Merchant merchant, Customer customer) {
        var authToken = this.authenticate();
        var stkRequest = buildMpesaStkRequest(paymentRequest);

        RestClient restClient = RestClient
                .builder()
                .baseUrl(mpesaConfig.getStkPushUrl())
                .build();


        var stkResponse = restClient.post()
                    .headers(httpHeaders -> {
                        httpHeaders.setBearerAuth(authToken);
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    })
                    .body(stkRequest)
                    .retrieve()
                    .body(MpesaStkResponse.class);


        var transactionRequest = new CreateTransactionRequest(
                "PENDING",
                customer.getId(),
                merchant.getId(),
                paymentRequest.getProvider(),
                paymentRequest.getAmount(),
                paymentRequest.getPaymentRef()
        );

        return transactionService.createTransaction(transactionRequest);
    }

    private TransactionDto initiatePayBillPayment(PaymentRequest paymentRequest, Merchant merchant, Customer customer) {
        return new TransactionDto();
    }

    private MpesaStkRequest buildMpesaStkRequest(PaymentRequest paymentRequest) {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        String shortCode = mpesaConfig.getStkShortCode();
        String passKey = mpesaConfig.getPassKey();
        String rawPassword = shortCode + passKey + timestamp;
        String password = Base64.getEncoder().encodeToString(rawPassword.getBytes(StandardCharsets.ISO_8859_1));

        var callBackUrl = mpesaConfig.getCallBackUrl();
        var accountRef = paymentRequest.getPaymentRef();
        var phoneNumber = paymentRequest.getMobileMoney().getPhoneNumber();
        String amount = String.valueOf(paymentRequest.getAmount().setScale(0, RoundingMode.CEILING).intValue());
        var desc = paymentRequest.getAmount() + " paid by " + paymentRequest.getName() + " for transaction " + paymentRequest.getPaymentRef();


        return new MpesaStkRequest(shortCode, password, timestamp, "CustomerPayBillOnline", amount, phoneNumber, shortCode, phoneNumber,  callBackUrl, accountRef, desc);
    }


    public Customer buildCustomerAccount(PaymentRequest paymentRequest, Merchant merchant) {
        var createCustomerDto = new CreateCustomerDto(
                paymentRequest.getName(),
                paymentRequest.getEmailAddress(),
                paymentRequest.getMobileMoney().getPhoneNumber()
        );

        return customerService.createCustomer(createCustomerDto, merchant);
    }
}