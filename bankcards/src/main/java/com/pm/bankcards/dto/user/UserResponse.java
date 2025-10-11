package com.pm.bankcards.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация о пользователе")
public record UserResponse(
        @Schema(example = "2", description = "ID пользователя")
        Long id,
        @Schema(example = "Patrick", description = "Имя пользователя")
        String username,
        @Schema(example = "[\"USER\"]", description = "Роли (админ, пользователь)")
        com.pm.bankcards.security.RoleName roles,
        @Schema(example = "true", description = "Активирован ли пользователь")
        boolean enabled
) {}
