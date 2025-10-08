package com.pm.bankcards.security;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тип ролей")
public enum RoleName {
    @Schema(description = "Администратор системы")
    ADMIN,
    @Schema(description = "Обычный пользователь")
    USER
}
