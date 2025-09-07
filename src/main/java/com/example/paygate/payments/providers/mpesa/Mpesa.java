package com.example.paygate.payments.providers.mpesa;

import com.example.paygate.customers.CustomerService;
import com.example.paygate.customers.dtos.CreateCustomerRequest;
import com.example.paygate.customers.dtos.CustomerDto;
import com.example.paygate.exceptions.PaymentProviderException;
import com.example.paygate.merchants.Merchant;
import com.example.paygate.payments.enums.PaymentProviderType;
import com.example.paygate.payments.providers.mpesa.dtos.*;
import com.example.paygate.transactions.TransactionsService;
import com.example.paygate.transactions.dtos.CreateTransactionDto;
import com.example.paygate.transactions.dtos.TransactionDto;
import com.example.paygate.payments.dtos.PaymentRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@Service
@AllArgsConstructor
public class Mpesa implements com.example.paygate.payments.providers.PaymentProvider<MpesaResponse> {

    private final MpesaConfig mpesaConfig;
    private final CustomerService customerService;
    private final TransactionsService transactionsService;

    private static final Logger logger = LoggerFactory.getLogger(Mpesa.class);


    @Override
    public String authenticate() {
        try {
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
                    .body(AuthResponse.class);

            return authResponse.getAccessToken();

        } catch (RestClientResponseException e) {
            logger.error("Mpesa authentication failed. Status: {} Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new PaymentProviderException(
                    "Mpesa authentication failed. Status: " + e.getStatusCode() + " Body: " + e.getResponseBodyAsString()
            );
        }
        catch (RestClientException e) {
            logger.error("Mpesa authentication failed. Status: {} Body: {}", e.getMessage());
            throw new PaymentProviderException("Unable to reach Mpesa");
        }
    }

    @Override
    public TransactionDto initiatePayment(PaymentRequest paymentRequest, Merchant merchant) {
        var stkRequest = buildMpesaStkRequest(paymentRequest);
        var stkResponse = initiateStkPayment(stkRequest);
        var customer = buildCustomerAccount(paymentRequest, merchant);


        var createTransactionDto = new CreateTransactionDto(
                merchant.getId(),
                customer.getId(),
                paymentRequest.getAmount(),
                paymentRequest.getProvider(),
                paymentRequest.getPaymentRef(),
                stkResponse.getMerchantRequestID()
        );

        return transactionsService.createTransaction(createTransactionDto);
    }

    @Override
    public void callback(MpesaResponse mpesaResponse) {

    }

    @Override
    public String checkPaymentStatus() {
        return "";
    }

    @Override
    public PaymentProviderType getProviderType() {
        return PaymentProviderType.MPESA;
    }

    private StkResponse initiateStkPayment(StkRequest stkRequest) {
        var authToken = this.authenticate();
        try {
            RestClient restClient = RestClient
                    .builder()
                    .baseUrl(mpesaConfig.getStkPushUrl())
                    .build();

            return restClient.post()
                    .headers(httpHeaders -> {
                        httpHeaders.setBearerAuth(authToken);
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    })
                    .body(stkRequest)
                    .retrieve()
                    .body(StkResponse.class);

        } catch (RestClientResponseException e) {
            logger.error("Mpesa STK request failed. Status: {} Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new PaymentProviderException(
                    "Mpesa STK request failed with status: " + e.getStatusCode() + " Body: " + e.getResponseBodyAsString()
            );
        }
        catch (RestClientException e) {
            logger.error("Unable to reach Mpesa: {}", e.getMessage(), e.getMessage());
            throw new PaymentProviderException("Unable to reach Mpesa STK service");
        }
    }

    private StkRequest buildMpesaStkRequest(PaymentRequest paymentRequest) {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        String shortCode = mpesaConfig.getShortCode();
        String passKey = mpesaConfig.getPassKey();
        String rawPassword = shortCode + passKey + timestamp;
        String password = Base64.getEncoder().encodeToString(rawPassword.getBytes(StandardCharsets.ISO_8859_1));

        var callBackUrl = mpesaConfig.getCallBackUrl();
        var accountRef = paymentRequest.getPaymentRef();
        var phoneNumber = paymentRequest.getMobileMoney().getPhoneNumber();
        String amount = paymentRequest.getAmount().toString();
        var desc = "Pay for " + paymentRequest.getPaymentRef();

        return new StkRequest(shortCode, password, timestamp, "CustomerPayBillOnline", amount, phoneNumber, shortCode, phoneNumber,  callBackUrl, accountRef, desc);
    }

    private CustomerDto buildCustomerAccount(PaymentRequest paymentRequest, Merchant merchant) {
        var customerRequest = new CreateCustomerRequest(
                paymentRequest.getName(),
                paymentRequest.getEmailAddress(),
                merchant.getId(),
                paymentRequest.getMobileMoney().getPhoneNumber()
        );

        return customerService.createCustomer(customerRequest);
    }
}