package com.example.paygate.users;

import com.example.paygate.exceptions.EmailAlreadyExistException;
import com.example.paygate.exceptions.dtos.ErrorDto;
import com.example.paygate.users.dtos.RegisterUserRequest;
import com.example.paygate.users.dtos.UserDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
    public ResponseEntity<UserDto> createUser(
           @Valid @RequestBody RegisterUserRequest request,
           UriComponentsBuilder uriComponentsBuilder
    ) {
        var userDto = usersService.createUser(request);
        var uri = uriComponentsBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ErrorDto> handleEmailAlreadyExist() {
        return ResponseEntity.badRequest().body(
                new ErrorDto("Email already exist")
        );
    }
}
