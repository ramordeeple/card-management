package com.pm.bankcards.security;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Role type")
public enum RoleName {
    ADMIN,
    USER;

    public String authority() {
        return "ROLE_" + name();
    }
}
