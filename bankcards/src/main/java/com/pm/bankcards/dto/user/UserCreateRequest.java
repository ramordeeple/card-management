package com.pm.bankcards.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Create user (admin)")
public record UserCreateRequest(
        @Schema(example = "Bob", description = "User name")
        @NotBlank(message = "Name required")
        String username,

        @Schema(example = "qwerty123", description = "Password")
        @NotBlank
        @Size(min = 8, message = "Password should contain at least 8 characters")
        String password
) {}
