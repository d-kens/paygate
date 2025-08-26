package com.example.paygate.users;

import com.example.paygate.users.dtos.RegisterUserRequest;
import com.example.paygate.users.dtos.UserDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/users")
public class UserController {

    private final UsersService usersService;

    @GetMapping
    public String getUsers() {
        return "These are the users we have";
    }

    @PostMapping
    public UserDto createUser(
           @Valid @RequestBody RegisterUserRequest request
    ) {
        return usersService.createUser(request);
    }
}
