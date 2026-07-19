package com.pm.bankcards.security;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Card status")
public enum CardStatus {
    ACTIVE, BLOCKED, EXPIRED
}
