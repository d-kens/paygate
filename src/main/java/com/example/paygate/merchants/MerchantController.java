package com.example.paygate.merchants;


import com.example.paygate.merchants.dtos.CreateMerchantRequest;
import com.example.paygate.merchants.dtos.MerchantDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    @PostMapping
    public MerchantDto createMerchant(@Valid @RequestBody CreateMerchantRequest request) {
        return merchantService.createMerchant(request);
    }

}
