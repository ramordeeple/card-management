package com.pm.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с JWT-токеном")
public record TokenResponse(
   @Schema(
           example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
           description = "JWT токен для авторизации"
   )
   String token,

   @Schema(
           example = "Bearer",
           description = "Тип токена (обычно 'Bearer'")
   String type
) {
    public TokenResponse(String token) {
        this(token, "Bearer");
    }
}
