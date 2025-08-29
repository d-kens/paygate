package com.example.paygate.merchants.mappers;

import com.example.paygate.merchants.Merchant;
import com.example.paygate.merchants.dtos.CreateMerchantRequest;
import com.example.paygate.merchants.dtos.MerchantDto;
import com.example.paygate.merchants.dtos.UpdateMerchantRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MerchantMapper {
    Merchant toEntity(CreateMerchantRequest request);

    @Mapping(source = "user.id", target = "userId")
    MerchantDto toDto(Merchant merchant);

    void update(UpdateMerchantRequest request, @MappingTarget Merchant merchant);
}
