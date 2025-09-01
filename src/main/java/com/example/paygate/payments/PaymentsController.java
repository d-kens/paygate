package com.example.paygate.payments;

import com.example.paygate.merchants.Merchant;
import com.example.paygate.payments.dtos.PaymentDto;
import com.example.paygate.payments.dtos.PaymentRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/payments")
public class PaymentsController {

    private final PaymentsService paymentsService;

    @PostMapping("/initiate")
    public PaymentDto initiatePayment(
            HttpServletRequest request,
            @Valid @RequestBody PaymentRequest paymentRequest
    ) {
        Merchant merchant = (Merchant) request.getAttribute("merchant");
        return paymentsService.initiatePayment(paymentRequest, merchant);
    }
}
