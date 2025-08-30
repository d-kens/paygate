package com.example.paygate.auth;

import com.example.paygate.auth.dtos.AccessToken;
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
    private JwtService jwtService;
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

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(accessToken.toString(), refreshToken.toString());
    }

    public AccessToken refreshAccessToken(String refreshToken) {
        var jwt = jwtService.parseToken(refreshToken);

        if (jwt == null)
            return null;

        var user = userRepository.findById(jwt.getUserId()).orElse(null);

        if (user == null)
            return null;

        return new AccessToken(jwtService.generateAccessToken(user).toString());
    }
}

