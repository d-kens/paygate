package com.example.paygate.merchants;

import com.example.paygate.exceptions.MerchantAlreadyExistsException;
import com.example.paygate.exceptions.NotFoundException;
import com.example.paygate.merchants.dtos.CreateMerchantRequest;
import com.example.paygate.merchants.dtos.MerchantDto;
import com.example.paygate.merchants.dtos.UpdateMerchantRequest;
import com.example.paygate.merchants.mappers.MerchantMapper;
import com.example.paygate.users.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MerchantService {
    private final MerchantMapper merchantMapper;
    private final MerchantRepository merchantRepository;
    private final UserRepository userRepository;


    public List<MerchantDto> findAll() {
        return merchantRepository.findAll().stream().map(merchantMapper::toDto).toList();
    }

    public MerchantDto findOne(Long merchantId) {
        var merchant = merchantRepository.findById(merchantId).orElseThrow(
                () -> new NotFoundException("Merchant with ID " + merchantId + " not found")
        );

        return merchantMapper.toDto(merchant);
    }


    public MerchantDto createMerchant(CreateMerchantRequest request) {
        var merchant = merchantMapper.toEntity(request);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (merchantRepository.existsByUserId(2L)) {
            throw new MerchantAlreadyExistsException();
        }

        merchant.setUser(user);
        merchant.generateApiKey();
        merchant.generateSecretKey();
        merchant.setWebhookActive(true);
        merchantRepository.save(merchant);

        return merchantMapper.toDto(merchant);
    }

    public MerchantDto updateMerchant(Long merchantId, UpdateMerchantRequest request) {
        var merchant = merchantRepository.findById(merchantId).orElseThrow(
                () -> new NotFoundException("Merchant with ID " + merchantId + " not found")
        );

        merchantMapper.update(request, merchant);
        merchantRepository.save(merchant);

        return merchantMapper.toDto(merchant);
    }

    public void deleteMerchant(Long merchantId) {
        var merchant = merchantRepository.findById(merchantId).orElseThrow(
                () -> new NotFoundException("Merchant with ID " + merchantId + " not found")
        );
        merchantRepository.delete(merchant);
    }
}
