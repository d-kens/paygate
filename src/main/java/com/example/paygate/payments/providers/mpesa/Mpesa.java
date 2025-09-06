package com.example.paygate.payments.providers.mpesa;

import com.example.paygate.customers.CustomerService;
import com.example.paygate.customers.dtos.CreateCustomerRequest;
import com.example.paygate.customers.dtos.CustomerDto;
import com.example.paygate.exceptions.PaymentProviderException;
import com.example.paygate.merchants.Merchant;
import com.example.paygate.payments.enums.PaymentProviderType;
import com.example.paygate.payments.providers.mpesa.dtos.*;
import com.example.paygate.transactions.TransactionStatus;
import com.example.paygate.transactions.TransactionsRepository;
import com.example.paygate.transactions.TransactionsService;
import com.example.paygate.transactions.dtos.CreateTransactionRequest;
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

import java.math.RoundingMode;
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
    private final TransactionsRepository transactionsRepository;

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
        var customer = buildCustomerAccount(paymentRequest, merchant);

        var transactionRequest = new CreateTransactionRequest(
                "PENDING",
                customer.getId(),
                merchant.getId(),
                paymentRequest.getProvider(),
                paymentRequest.getAmount().setScale(0, RoundingMode.CEILING),
                paymentRequest.getPaymentRef()
        );

        var transactionDto = transactionsService.createTransaction(transactionRequest);
        var stkRequest = buildMpesaStkRequest(paymentRequest, transactionDto);

        System.out.println("oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo");
        initiateStkPayment(stkRequest);
        return transactionDto;
    }

    @Override
    public void callback(MpesaResponse mpesaResponse) {
        Long transactionId = Long.parseLong(mpesaResponse.getBody().getStkCallBack().getMerchantRequestID());

        transactionsRepository.findById(transactionId).ifPresent(transaction -> {
            var resultCode = mpesaResponse.getBody().getStkCallBack().getResultCode();
            var checkoutRequestID = mpesaResponse.getBody().getStkCallBack().getCheckoutRequestID();
            var resultDesc = mpesaResponse.getBody().getStkCallBack().getResultDesc();

            switch (resultCode) {
                case 0 -> {
                    transaction.setStatus(TransactionStatus.SUCCESS);
                    transaction.setProviderTransactionId(checkoutRequestID);
                    transaction.setDescription(resultDesc);
                }
                case 1032 -> {
                    transaction.setStatus(TransactionStatus.CANCELLED);
                    transaction.setProviderTransactionId(checkoutRequestID);
                    transaction.setDescription(resultDesc);
                }
                default -> {
                    transaction.setStatus(TransactionStatus.FAILED);
                }
            }

            transactionsRepository.save(transaction);

            // TODO: POST WebHook event to the merchant
            // TODO: POST Payment notification to customer


        });
    }

    @Override
    public String checkPaymentStatus() {
        return "";
    }

    @Override
    public PaymentProviderType getProviderType() {
        return PaymentProviderType.MPESA;
    }

    private void initiateStkPayment(StkRequest stkRequest) {
        var authToken = this.authenticate();
        try {
            RestClient restClient = RestClient
                    .builder()
                    .baseUrl(mpesaConfig.getStkPushUrl())
                    .build();

            var stkResponse =  restClient.post()
                    .headers(httpHeaders -> {
                        httpHeaders.setBearerAuth(authToken);
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    })
                    .body(stkRequest)
                    .retrieve()
                    .body(StkResponse.class);


            System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
            System.out.println(stkResponse);
            System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");

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

    private StkRequest buildMpesaStkRequest(PaymentRequest paymentRequest, TransactionDto transactionDto) {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        String shortCode = mpesaConfig.getShortCode();
        String passKey = mpesaConfig.getPassKey();
        String rawPassword = shortCode + passKey + timestamp;
        String password = Base64.getEncoder().encodeToString(rawPassword.getBytes(StandardCharsets.ISO_8859_1));

        var callBackUrl = mpesaConfig.getCallBackUrl();
        var accountRef = transactionDto.getId().toString();
        var phoneNumber = paymentRequest.getMobileMoney().getPhoneNumber();
        String amount = transactionDto.getAmount().toString();
        var desc = "Pay for " + transactionDto.getPaymentReference();

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