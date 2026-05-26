package com.pm.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Current user info")
public record MeResponse(
        @Schema(example = "1", description = "User ID") Long id,
        @Schema(example = "admin", description = "User name") String username,
        @Schema(example = "ADMIN", description = "User role") String role
) {}
