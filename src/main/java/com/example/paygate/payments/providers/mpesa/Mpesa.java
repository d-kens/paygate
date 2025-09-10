package com.example.paygate.payments.providers.mpesa;

import com.example.paygate.customers.CustomerService;
import com.example.paygate.customers.dtos.CreateCustomerRequest;
import com.example.paygate.customers.dtos.CustomerDto;
import com.example.paygate.exceptions.PaymentProviderException;
import com.example.paygate.merchants.Merchant;
import com.example.paygate.payments.enums.PaymentProviderType;
import com.example.paygate.payments.providers.PaymentProvider;
import com.example.paygate.payments.providers.mpesa.dtos.*;
import com.example.paygate.transactions.Transaction;
import com.example.paygate.transactions.TransactionStatus;
import com.example.paygate.transactions.TransactionsRepository;
import com.example.paygate.transactions.TransactionsService;
import com.example.paygate.transactions.dtos.CreateTransactionDto;
import com.example.paygate.transactions.dtos.TransactionDto;
import com.example.paygate.payments.dtos.PaymentRequest;
import com.example.paygate.webhook.WebhookEventService;
import com.example.paygate.webhook.dtos.WebhookPayload;
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
import java.util.Optional;

@Service
@AllArgsConstructor
public class Mpesa implements PaymentProvider<MpesaResponse> {

    private final MpesaConfig mpesaConfig;
    private final CustomerService customerService;
    private final WebhookEventService webhookEventService;
    private final TransactionsService transactionsService;
    private final TransactionsRepository transactionsRepository;

    private static final Logger logger = LoggerFactory.getLogger(Mpesa.class);

    @Override
    public PaymentProviderType getProviderType() {
        return PaymentProviderType.MPESA;
    }

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

        try {
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

        } catch (PaymentProviderException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to initiate Mpesa payment for merchantId={} paymentRef={}",
                    merchant.getId(), paymentRequest.getPaymentRef(), e);
            throw new PaymentProviderException("Failed to initiate Mpesa payment");
        }
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
            logger.error("Unable to reach Mpesa: {}", e.getMessage());
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

        try {
            var customerRequest = new CreateCustomerRequest(
                    paymentRequest.getName(),
                    paymentRequest.getEmailAddress(),
                    merchant.getId(),
                    paymentRequest.getMobileMoney().getPhoneNumber()
            );

            return customerService.createCustomer(customerRequest);

        } catch (Exception e) {
            logger.error("Failed to create customer for merchantId={} paymentRef={}",
                    merchant.getId(), paymentRequest.getPaymentRef(), e);
            throw new PaymentProviderException("Failed to create customer for Mpesa payment");
        }
    }

    @Override
    public void callback(MpesaResponse mpesaResponse) {
        try {
            if (mpesaResponse == null || mpesaResponse.getBody() == null || mpesaResponse.getBody().getStkCallBack() == null) {
                logger.warn("Invalid Mpesa callback received: {}", mpesaResponse);
                return;
            }

            String providerReferenceId = mpesaResponse.getBody().getStkCallBack().getMerchantRequestID();
            Optional<Transaction> result = transactionsRepository.findByProviderReferenceId(providerReferenceId);


            if (!result.isPresent()) {
                logger.error("No transaction found for the providerReferenceId={}", providerReferenceId);
                return;
            }

            var transaction = result.get();

            int resultCode = mpesaResponse.getBody().getStkCallBack().getResultCode();

            switch (resultCode) {
                case 0:
                    transaction.setStatus(TransactionStatus.SUCCESS);
                    transaction.setDescription(mpesaResponse.getBody().getStkCallBack().getResultDesc());

                    MpesaResponse.CallBackMetaData callBackMetaData = mpesaResponse.getBody().getStkCallBack().getCallBackMetaData();


                    if (callBackMetaData != null && callBackMetaData.getItem() != null) {
                        for (MpesaResponse.Item item : callBackMetaData.getItem()) {
                            if ("MpesaReceiptNumber".equals(item.getName())) {
                                var existing = transactionsRepository.findByProviderTransactionId(item.getValue());

                                if (existing.isPresent()) {
                                    if (existing.get().getId().equals(transaction.getId())) {
                                        logger.info("Callback already processed for transaction {} with receipt {}",
                                                transaction.getId(), item.getValue());
                                    } else {
                                        throw new PaymentProviderException(
                                                "Duplicate provider transaction ID " + item.getValue() +
                                                        " found in transaction " + existing.get().getId()
                                        );
                                    }
                                } else {
                                    transaction.setProviderTransactionId(item.getValue());
                                }
                            }

                            if ("PhoneNumber".equals(item.getName()))
                                transaction.setPaidBy(item.getValue());
                        }
                    }
                    break;
                case 1032:
                    transaction.setStatus(TransactionStatus.CANCELLED);
                    transaction.setDescription(mpesaResponse.getBody().getStkCallBack().getResultDesc());
                    break;
                default:
                    transaction.setStatus(TransactionStatus.FAILED);
                    break;
            }

            Transaction updatedTransaction =  transactionsService.updateTransaction(transaction);

            var webhookPayload = new WebhookPayload(
                    updatedTransaction.getId(),
                    updatedTransaction.getStatus().toString(),
                    updatedTransaction.getAmount(),
                    updatedTransaction.getCurrency(),
                    updatedTransaction.getMerchantPaymentReference()
            );

            webhookEventService.createAndDispatch(
                    updatedTransaction.getId(),
                    updatedTransaction.getMerchant().getId(),
                    updatedTransaction.getMerchant().getWebhookUrl(),
                    webhookPayload
            );
        } catch (PaymentProviderException e) {
            logger.warn("Error while processing Mpesa callback: {}", e.getMessage());
        }
    }
}