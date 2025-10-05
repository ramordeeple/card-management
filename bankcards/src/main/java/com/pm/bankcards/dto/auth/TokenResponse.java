package com.pm.bankcards.dto.auth;

public record TokenResponse(
   String token,
   String type
) {
    public TokenResponse(String token) {
        this(token, "Bearer");
    }
}
