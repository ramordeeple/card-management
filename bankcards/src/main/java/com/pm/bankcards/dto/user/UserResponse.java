package com.pm.bankcards.dto.user;

import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        Set<String> roles,
        boolean enabled
) {}
