package com.example.paygate.payments.providers.mpesa;

import com.example.paygate.customers.Customer;
import com.example.paygate.customers.CustomerService;
import com.example.paygate.customers.dtos.CreateCustomerDto;
import com.example.paygate.exceptions.PaymentProviderException;
import com.example.paygate.merchants.Merchant;
import com.example.paygate.payments.providers.mpesa.dtos.*;
import com.example.paygate.transactions.TransactionRepository;
import com.example.paygate.transactions.TransactionService;
import com.example.paygate.transactions.dtos.CreateTransactionRequest;
import com.example.paygate.transactions.dtos.TransactionDto;
import com.example.paygate.payments.dtos.PaymentRequest;
import com.example.paygate.payments.enums.TransactionType;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@Service
@AllArgsConstructor
public class Mpesa implements PaymentProvider {
    private static final Logger logger = LoggerFactory.getLogger(Mpesa.class);

    private final MpesaConfig mpesaConfig;
    private final CustomerService customerService;
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

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

        if (paymentRequest.getMobileMoney().getTransactionType().equals(TransactionType.STK.name())) {
            initiateStkPayment(paymentRequest, merchant, customer);
        }

        return buildAndSaveTransaction(paymentRequest, merchant, customer);
    }

    @Override
    public String callback() {
        return "";
    }

    @Override
    public String checkPaymentStatus() {
        return "";
    }

    private void initiateStkPayment(PaymentRequest paymentRequest, Merchant merchant, Customer customer) {
        var authToken = this.authenticate();
        try {
            var stkRequest = buildMpesaStkRequest(paymentRequest);

            RestClient restClient = RestClient
                    .builder()
                    .baseUrl(mpesaConfig.getStkPushUrl())
                    .build();

            restClient.post()
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

    private TransactionDto buildAndSaveTransaction(PaymentRequest paymentRequest, Merchant merchant, Customer customer) {
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

    private StkRequest buildMpesaStkRequest(PaymentRequest paymentRequest) {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        String shortCode = mpesaConfig.getShortCode();
        String passKey = mpesaConfig.getPassKey();
        String rawPassword = shortCode + passKey + timestamp;
        String password = Base64.getEncoder().encodeToString(rawPassword.getBytes(StandardCharsets.ISO_8859_1));

        var callBackUrl = mpesaConfig.getCallBackUrl();
        var accountRef = paymentRequest.getPaymentRef();
        var phoneNumber = paymentRequest.getMobileMoney().getPhoneNumber();
        String amount = String.valueOf(paymentRequest.getAmount().setScale(0, RoundingMode.CEILING).intValue());
        var desc = paymentRequest.getAmount() + " paid by " + paymentRequest.getName() + " for transaction " + paymentRequest.getPaymentRef();

        return new StkRequest(shortCode, password, timestamp, "CustomerPayBillOnline", amount, phoneNumber, shortCode, phoneNumber,  callBackUrl, accountRef, desc);
    }

    private Customer buildCustomerAccount(PaymentRequest paymentRequest, Merchant merchant) {
        var createCustomerDto = new CreateCustomerDto(
                paymentRequest.getName(),
                paymentRequest.getEmailAddress(),
                paymentRequest.getMobileMoney().getPhoneNumber()
        );

        return customerService.createCustomer(createCustomerDto, merchant);
    }

    public void registerPayBillUrls() {
        logger.info("Registering MPESA PayBill URLs");
        var authToken = this.authenticate();

        try {
            RestClient restClient = RestClient.builder()
                    .baseUrl(mpesaConfig.getRegisterPaybillUrl())
                    .build();

            var registerPayBillUrlReq = new RegisterUrlRequest(
                    mpesaConfig.getShortCode(),
                    "Completed",
                    mpesaConfig.getConfirmationUrl(),
                    mpesaConfig.getValidationUrl()
            );

            var response = restClient.post()
                    .headers(httpHeaders -> {
                        httpHeaders.setBearerAuth(authToken);
                        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    })
                    .body(registerPayBillUrlReq)
                    .retrieve()
                    .body(RegisterUrlResponse.class);

            logger.info(response.toString());


        } catch (RestClientResponseException e) {
            logger.error("Register URL failed. Status: {} Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (RestClientException e) {
            logger.error("Register URL failed. Error: {}", e.getMessage());
        }
    }

    public ConfirmationValidation confirmPayBillPayment(PayBillResponse payBillResponse) {
        var transaction = transactionService.findTransactionByPaymentRef(payBillResponse.getBillRefNumber());

        if (transaction == null) {
            logger.warn("Confirmation received for unknown transaction: {}", payBillResponse.getBillRefNumber());
            return new ConfirmationValidation("0", "Success");
        }

        var paidAmount = new BigDecimal(payBillResponse.getTransAmount());

        if (transaction.getAmount().compareTo(paidAmount) != 0) {
            logger.warn("Confirmation amount mismatch. Expected: {}, Received: {} for ref: {}",
                    transaction.getAmount(), paidAmount, payBillResponse.getBillRefNumber());
            return new ConfirmationValidation("0", "Success");
        }

        transaction.setStatus("PAID");
        transaction.setProviderTransactionId(payBillResponse.getTransID());
        transactionRepository.save(transaction);

        logger.info("Payment confirmed successfully for transaction: {}", payBillResponse.getBillRefNumber());
        return new ConfirmationValidation("0", "Success");
    }
}