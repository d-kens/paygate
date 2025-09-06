package com.example.paygate.users;

import com.example.paygate.users.dtos.RegisterUserRequest;
import com.example.paygate.users.dtos.UpdateUserRequest;
import com.example.paygate.users.dtos.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(RegisterUserRequest request);
    UserDto toDto(User user);
    void update(UpdateUserRequest request, @MappingTarget User user);
}
