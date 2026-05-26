package com.pm.bankcards.dto.card;

import com.pm.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cards search filters")
public record CardFilter(
        @Schema(example = "4242", description = "Card last 4 digits")
        String last4,
        @Schema(example = "ACTIVE", description = "Card status")
        CardStatus status,
        @Schema(example = "2030", description = "Expiration year")
        Integer expiryYear
) {}
