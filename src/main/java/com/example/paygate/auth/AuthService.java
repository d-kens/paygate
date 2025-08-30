package com.example.paygate.auth;

import com.example.paygate.auth.dtos.AuthRequest;
import com.example.paygate.auth.dtos.AuthResponse;
import com.example.paygate.users.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()
                )
        );
        var user = userRepository.findByEmail(authRequest.getEmail()).orElseThrow();

        String accessToken = "etryuioudsdfghjklewrtyui";
        String refreshToken = "etryuioudsdfghjklewrtyui";


        return new AuthResponse(accessToken, refreshToken);
    }
}

