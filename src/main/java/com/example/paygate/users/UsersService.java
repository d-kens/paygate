package com.example.paygate.users;

import com.example.paygate.exceptions.EmailAlreadyExistException;
import com.example.paygate.exceptions.NotFoundException;
import com.example.paygate.exceptions.PasswordMismatchException;
import com.example.paygate.users.dtos.ChangePasswordRequest;
import com.example.paygate.users.dtos.RegisterUserRequest;
import com.example.paygate.users.dtos.UpdateUserRequest;
import com.example.paygate.users.dtos.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UsersService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    public UserDto findById(Long userid) {
        var user = userRepository.findById(userid).orElse(null);
        if (user == null) throw new NotFoundException("User with ID: " + userid + " not found");
        return userMapper.toDto(user);
    }

    public UserDto createUser(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistException();
        }

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public UserDto updateUser(Long userid, UpdateUserRequest request) {
        var user = userRepository.findById(userid).orElse(null);

        if (user == null) throw new NotFoundException("User with ID " + userid + " not found");

        userMapper.update(request, user);
        userRepository.save(user);

        return userMapper.toDto(user);
    }


    public void deleteUser(Long userid) {
        var user = userRepository.findById(userid).orElse(null);

        if (user == null) throw new NotFoundException("User with ID " + userid + " not found");


        userRepository.delete(user);
    }

    public void changePassword(Long userid, ChangePasswordRequest request) {
        var user = userRepository.findById(userid).orElse(null);

        if (user == null) throw new NotFoundException("User with ID " + userid + " not found");

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new PasswordMismatchException();

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
