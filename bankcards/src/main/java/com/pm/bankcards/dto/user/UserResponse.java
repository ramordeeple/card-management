package com.pm.bankcards.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User info")
public record UserResponse(
        @Schema(example = "2", description = "User ID")
        Long id,
        @Schema(example = "Patrick", description = "User name")
        String username,
        @Schema(example = "[\"USER\"]", description = "Roles (admin, user)")
        com.pm.bankcards.security.RoleName roles,
        @Schema(example = "true", description = "Is user active?")
        boolean enabled
) {}
