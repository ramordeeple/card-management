package com.pm.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация о текущем пользователе")
public record MeResponse(
        @Schema(example = "1", description = "ID пользователя") Long id,
        @Schema(example = "admin", description = "Имя пользователя") String username,
        @Schema(example = "ADMIN", description = "Роль пользователя") String role
) {}
