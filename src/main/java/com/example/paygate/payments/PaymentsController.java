package com.example.paygate.payments;

import com.example.paygate.exceptions.PaymentProviderException;
import com.example.paygate.exceptions.dtos.ErrorDto;
import com.example.paygate.merchants.Merchant;
import com.example.paygate.payments.providers.mpesa.dtos.StkCallBackData;
import com.example.paygate.transactions.dtos.TransactionDto;
import com.example.paygate.payments.dtos.PaymentRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/payments")
public class PaymentsController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentsController.class);

    private final PaymentsService paymentsService;


    @PostMapping("/initiate")
    public TransactionDto initiatePayment(
            HttpServletRequest request,
            @Valid @RequestBody PaymentRequest paymentRequest
    ) {
        Merchant merchant = (Merchant) request.getAttribute("merchant");
        return paymentsService.initiatePayment(paymentRequest, merchant);
    }

    @PostMapping("/m_pesa/callback")
    public ResponseEntity<Void> processMpesaCallback(@RequestBody StkCallBackData data) {
        logger.info("Received STK Callback: " + data);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(PaymentProviderException.class)
    public ResponseEntity<ErrorDto> handlePaymentProviderException() {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                new ErrorDto("Payment initiation failed. Please try again later")
        );
    }
}
