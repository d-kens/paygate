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

    @GetMapping("/{userId}")
    public UserDto findBYId(@PathVariable Long userId) {
        return usersService.findById(userId);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(
           @Valid @RequestBody RegisterUserRequest request,
           UriComponentsBuilder uriComponentsBuilder
    ) {
        var userDto = usersService.createUser(request);
        var uri = uriComponentsBuilder.path("/users/{userId}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @PostMapping("/{userId}/change-password")
    public void changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        System.out.println(userId);
        usersService.changePassword(userId, request);
    }

    @PutMapping("/{userId}")
    public UserDto updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserRequest request
    ) {
        return usersService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userid) {
        usersService.deleteUser(userid);

        return ResponseEntity.noContent().build();
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
