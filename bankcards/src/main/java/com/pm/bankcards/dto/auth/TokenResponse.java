package com.pm.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT response")
public record TokenResponse(
   @Schema(
           example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
           description = "JWT for auth"
   )
   String token,

   @Schema(
           example = "Bearer",
           description = "Token type ('Bearer'")
   String type
) {
    public TokenResponse(String token) {
        this(token, "Bearer");
    }
}
