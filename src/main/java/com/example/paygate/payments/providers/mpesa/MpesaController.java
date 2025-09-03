package com.example.paygate.payments.providers.mpesa;

import com.example.paygate.payments.providers.mpesa.dtos.ConfirmationValidation;
import com.example.paygate.payments.providers.mpesa.dtos.PayBillResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/m_pesa")
public class MpesaController {
    private final Mpesa mpesa;

    @PostMapping("/confirm")
    public ConfirmationValidation confirmPayBillPayment(@RequestBody PayBillResponse payBillResponse) {
        return mpesa.confirmPayBillPayment(payBillResponse);
    }
}
