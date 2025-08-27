package com.example.paygate.users;

import com.example.paygate.exceptions.EmailAlreadyExistException;
import com.example.paygate.exceptions.NotFoundException;
import com.example.paygate.users.dtos.RegisterUserRequest;
import com.example.paygate.users.dtos.UpdateUserRequest;
import com.example.paygate.users.dtos.UserDto;
import com.example.paygate.users.mappers.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UsersService {

    private final UserMapper userMapper;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> findAll() {
        return usersRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    public UserDto findById(Long id) {
        var user = usersRepository.findById(id).orElse(null);
        if (user == null) throw new NotFoundException("User with ID: " + id + " not found");
        return userMapper.toDto(user);
    }

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

    public UserDto updateUser(Long id, UpdateUserRequest request) {
        var user = usersRepository.findById(id).orElse(null);

        if (user == null) throw new NotFoundException("User with ID " + id + " not found");

        userMapper.update(request, user);
        usersRepository.save(user);

        return userMapper.toDto(user);
    }


    public void deleteUser(Long id) {
        var user = usersRepository.findById(id).orElse(null);

        if (user == null) {
            throw new NotFoundException("User with ID " + id + " not found");
        }

        usersRepository.delete(user);
    }
}
