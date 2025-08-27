package com.example.paygate.users;

import com.example.paygate.exceptions.EmailAlreadyExistException;
import com.example.paygate.users.dtos.RegisterUserRequest;
import com.example.paygate.users.dtos.UserDto;
import com.example.paygate.users.mappers.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UsersService {

    private final UserMapper userMapper;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto createUser(RegisterUserRequest request) {
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistException();
        }

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        usersRepository.save(user);

        return userMapper.toDto(user);
    }
}
