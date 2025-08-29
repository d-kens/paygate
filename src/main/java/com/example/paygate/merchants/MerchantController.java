package com.example.paygate.merchants;


import com.example.paygate.merchants.dtos.CreateMerchantRequest;
import com.example.paygate.merchants.dtos.MerchantDto;
import com.example.paygate.merchants.dtos.UpdateMerchantRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    @GetMapping
    public List<MerchantDto> findAll() {
        return merchantService.findAll();
    }

    @GetMapping("/{id}")
    public MerchantDto findOne(@PathVariable Long id) {
        return merchantService.findOne(id);
    }

    @PostMapping
    public ResponseEntity<MerchantDto> createMerchant(
            @Valid @RequestBody CreateMerchantRequest request,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        var merchantDto = merchantService.createMerchant(request);
        var uri = uriComponentsBuilder.path("/merchants/{id}").buildAndExpand(merchantDto.getId()).toUri();
        return ResponseEntity.created(uri).body(merchantDto);
    }

    @PutMapping("/{id}")
    public MerchantDto updateMerchant(
            @PathVariable Long id,
            @RequestBody UpdateMerchantRequest request
    ) {
        return merchantService.updateMerchant(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        merchantService.deleteMerchant(id);
        return ResponseEntity.noContent().build();
    }

}
