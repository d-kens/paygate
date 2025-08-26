package com.example.paygate.users;


import com.example.paygate.users.dtos.RegisterUserRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @GetMapping
    public String getUsers() {
        return "These are the users we have";
    }


    @PostMapping
    public String createUser(
           @RequestBody RegisterUserRequest request
    ) {
        System.out.println("This is the register user request");
        System.out.println(request);
        return  "User Created Successfully";
    }
}
