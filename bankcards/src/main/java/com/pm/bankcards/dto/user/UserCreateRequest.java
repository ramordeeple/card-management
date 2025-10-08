package com.pm.bankcards.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Создание пользователя (админ)")
public record UserCreateRequest(
        @Schema(example = "Bob", description = "Имя пользователя")
        @NotBlank(message = "Требуется имя")
        String username,

        @Schema(example = "qwerty123", description = "Пароль")
        @NotBlank
        @Size(min = 8, message = "Пароль должен содержать хотя бы 8 символов")
        String password
) {}
