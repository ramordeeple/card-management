package com.pm.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запрос на вход")
public record LoginRequest(
        @Schema(example = "admin", description = "Имя пользователя")
        @NotBlank(message = "Name cannot be empty")
        String username,

        @Schema(example = "admin", description = "Пароль")
        @NotBlank(message = "Password cannot be empty")
        String password
) {}
