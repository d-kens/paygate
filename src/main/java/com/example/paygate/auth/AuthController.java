package com.example.paygate.auth;

import com.example.paygate.auth.dtos.AccessToken;
import com.example.paygate.auth.dtos.AuthRequest;
import com.example.paygate.users.UsersService;
import com.example.paygate.users.dtos.UserDto;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.BadCredentialsException;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private JwtService jwtService;
    private final AuthService authService;
    private final UsersService usersService;

    @PostMapping("/login")
    public ResponseEntity<AccessToken> login(
            @Valid @RequestBody AuthRequest authRequest,
            HttpServletResponse response
    ) {
        var authResponse = authService.login(authRequest);

        var cookie = new Cookie("refreshToken", authResponse.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(9000);
        cookie.setSecure(true);
        response.addCookie(cookie);


        return ResponseEntity.ok().body(
                new AccessToken(authResponse.getAccessToken())
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessToken> refreshToken(
            @CookieValue(value = "refreshToken") String refreshToken
    ) {
        var accessToken = authService.refreshAccessToken(refreshToken);

        if (accessToken == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(accessToken);
    }

    @GetMapping("/me")
    public UserDto me() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();
        return usersService.findById(userId);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
