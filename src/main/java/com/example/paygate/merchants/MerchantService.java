package com.example.paygate.merchants;

import com.example.paygate.exceptions.NotFoundException;
import com.example.paygate.merchants.dtos.CreateMerchantRequest;
import com.example.paygate.merchants.dtos.MerchantDto;
import com.example.paygate.merchants.dtos.UpdateMerchantRequest;
import com.example.paygate.merchants.mappers.MerchantMapper;
import com.example.paygate.users.UserRepository;
import lombok.AllArgsConstructor;
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

    public MerchantDto findOne(Long id) {
        var merchant = merchantRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Merchant with ID " + id + " not found")
        );

        return merchantMapper.toDto(merchant);
    }


    public MerchantDto createMerchant(CreateMerchantRequest request) {
        var merchant = merchantMapper.toEntity(request);

        var user = userRepository.findById(2L) // TODO: Update to use currently authenticated user
                .orElseThrow(() -> new RuntimeException("User not found"));

        merchant.setUser(user);
        merchant.generateApiKey();
        merchant.generateSecretKey();
        merchantRepository.save(merchant);

        return merchantMapper.toDto(merchant);
    }

    public MerchantDto updateMerchant(Long id, UpdateMerchantRequest request) {
        var merchant = merchantRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Merchant with ID " + id + " not found")
        );

        merchantMapper.update(request, merchant);
        merchantRepository.save(merchant);

        return merchantMapper.toDto(merchant);
    }

    public void deleteMerchant(Long id) {
        var merchant = merchantRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Merchant with ID " + id + " not found")
        );
        merchantRepository.delete(merchant);
    }
}
