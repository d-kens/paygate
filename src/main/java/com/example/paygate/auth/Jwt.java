package com.example.paygate.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

public class Jwt {
    private final Claims claims;
    private final SecretKey secretKey;

    public Jwt(Claims claims, SecretKey secretKey) {
        this.claims = claims;
        this.secretKey = secretKey;
    }

    public String toString() {
        return Jwts
                .builder()
                .claims(claims)
                .signWith(secretKey)
                .compact();
    }
}
