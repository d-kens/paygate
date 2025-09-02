package com.example.paygate.payments.providers.mpesa;

import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MpesaStartupRunner implements ApplicationRunner {

    private final Mpesa mpesa;

    @Override
    public void run(ApplicationArguments args) {
        mpesa.registerPayBillUrls();
    }

}
