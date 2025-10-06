package com.pm.bankcards.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Name cannot be empty")
        String username,

        @NotBlank(message = "Password cannot be empty")
        String password
) {}
