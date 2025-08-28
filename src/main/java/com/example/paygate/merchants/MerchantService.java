package com.example.paygate.merchants;

import com.example.paygate.merchants.dtos.CreateMerchantRequest;
import com.example.paygate.merchants.dtos.MerchantDto;
import com.example.paygate.merchants.mappers.MerchantMapper;
import com.example.paygate.users.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MerchantService {
    private final MerchantMapper merchantMapper;
    private final MerchantRepository merchantRepository;
    private final UserRepository userRepository;


    public MerchantDto createMerchant(CreateMerchantRequest request) {

        var merchant = merchantMapper.toEntity(request);
        System.out.println();

        var user = userRepository.findById(2L) // TODO: Update to use currently authenticated user
                .orElseThrow(() -> new RuntimeException("User not found"));

        merchant.setUser(user);
        merchantRepository.save(merchant);

        return merchantMapper.toDto(merchant);
    }
}
