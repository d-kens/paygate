package com.example.paygate.users;

import com.example.paygate.exceptions.EmailAlreadyExistException;
import com.example.paygate.exceptions.PasswordMismatchException;
import com.example.paygate.exceptions.dtos.ErrorDto;
import com.example.paygate.users.dtos.ChangePasswordRequest;
import com.example.paygate.users.dtos.RegisterUserRequest;
import com.example.paygate.users.dtos.UpdateUserRequest;
import com.example.paygate.users.dtos.UserDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UsersService usersService;

    @GetMapping
    public List<UserDto> findAll() {
        return usersService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findBYId(@PathVariable Long id) {
        return usersService.findById(id);
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

    @PutMapping("/{id}")
    public UserDto updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request
    ) {
        return usersService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        usersService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    public void changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        System.out.println(id);
        usersService.changePassword(id, request);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ErrorDto> handleEmailAlreadyExist() {
        return ResponseEntity.badRequest().body(
                new ErrorDto("Email already exist")
        );
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorDto> handlePasswordMismatchException() {
        return ResponseEntity.badRequest().body(
                new ErrorDto("Old password does not match")
        );
    }
}
