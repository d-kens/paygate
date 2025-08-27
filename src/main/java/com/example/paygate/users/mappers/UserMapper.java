package com.example.paygate.users.mappers;

import com.example.paygate.users.User;
import com.example.paygate.users.dtos.RegisterUserRequest;
import com.example.paygate.users.dtos.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(RegisterUserRequest request);
    UserDto toDto(User user);
}
