package com.pm.bankcards.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to log in")
public record LoginRequest(
        @Schema(example = "admin", description = "Username")
        @NotBlank(message = "Name cannot be empty")
        String username,

        @Schema(example = "admin", description = "Password")
        @NotBlank(message = "Password cannot be empty")
        String password
) {}
