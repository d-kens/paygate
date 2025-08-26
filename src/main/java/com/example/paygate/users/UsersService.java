package com.example.paygate.users;

import com.example.paygate.users.dtos.RegisterUserRequest;
import com.example.paygate.users.dtos.UserDto;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    public UserDto createUser(RegisterUserRequest request) {
        // check if user already exist and return a bad request
        // if user does not exist map the request to user entity
        // set user password
        // set user role
        // save the user
        // convert user to UserDto
        // return
        return new UserDto(10L, "Dummy", "dummy@gmail.com");
    }
}
