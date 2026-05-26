package com.pm.bankcards.dto.card;

import com.pm.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "User's card info")
public record CardResponse(
        @Schema(example = "42", description = "Card ID")
        Long id,
        @Schema(example = "**** **** **** 4242", description = "Masked card number")
        String maskedNumber,
        @Schema(example = "Alice", description = "Card owner's name")
        String ownerUsername,
        @Schema(example = "12", description = "Expiration month (1-12)")
        int expiryMonth,
        @Schema(example = "2030", description = "Expiration year")
        int expiryYear,
        @Schema(example = "ACTIVE", description = "Card status")
        CardStatus status,
        @Schema(example = "15420.50", description = "current balance")
        BigDecimal balance
) implements Serializable {}
