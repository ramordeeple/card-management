package com.pm.bankcards.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank(message = "Name is required")
        String username,

        @NotBlank
        @Size(min = 8, message = "Password should contain at least 8 symbols")
        String password
) {}
