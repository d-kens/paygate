package com.example.paygate.merchants;


import com.example.paygate.exceptions.MerchantAlreadyExistsException;
import com.example.paygate.exceptions.dtos.ErrorDto;
import com.example.paygate.merchants.dtos.CreateMerchantRequest;
import com.example.paygate.merchants.dtos.MerchantDto;
import com.example.paygate.merchants.dtos.UpdateMerchantRequest;
import com.example.paygate.transactions.TransactionsService;
import com.example.paygate.transactions.dtos.TransactionDto;
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
    private final TransactionsService transactionsService;

    @GetMapping
    public List<MerchantDto> findAll() {
        return merchantService.findAll();
    }

    @GetMapping("/{merchantId}")
    public MerchantDto findOne(@PathVariable Long merchantId) {
        return merchantService.findOne(merchantId);
    }

    @GetMapping("/{merchantId}/transactions")
    public List<TransactionDto> findTransactionsByMerchantId(@PathVariable Long merchantId) {
        return transactionsService.findTransactionsByMerchantId(merchantId);
    }

    @PostMapping
    public ResponseEntity<MerchantDto> createMerchant(
            @Valid @RequestBody CreateMerchantRequest request,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        var merchantDto = merchantService.createMerchant(request);
        var uri = uriComponentsBuilder.path("/merchants/{merchantId}").buildAndExpand(merchantDto.getId()).toUri();
        return ResponseEntity.created(uri).body(merchantDto);
    }

    @PutMapping("/{merchantId}")
    public MerchantDto updateMerchant(
            @PathVariable Long merchantId,
            @RequestBody UpdateMerchantRequest request
    ) {
        return merchantService.updateMerchant(merchantId, request);
    }

    @DeleteMapping("/{merchantId}")
    public ResponseEntity<Void> delete(@PathVariable Long merchantId) {
        merchantService.deleteMerchant(merchantId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(MerchantAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleMerchantAlreadyExist(MerchantAlreadyExistsException exception) {
        return ResponseEntity.badRequest().body(
                new ErrorDto(exception.getMessage())
        );
    }

}
