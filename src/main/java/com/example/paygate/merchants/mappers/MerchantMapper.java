package com.example.paygate.merchants.mappers;

import com.example.paygate.merchants.Merchant;
import com.example.paygate.merchants.dtos.CreateMerchantRequest;
import com.example.paygate.merchants.dtos.MerchantDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MerchantMapper {
    Merchant toEntity(CreateMerchantRequest request);

    @Mapping(source = "user.id", target = "user_id")
    MerchantDto toDto(Merchant merchant);
}
