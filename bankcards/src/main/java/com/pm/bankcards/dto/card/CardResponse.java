package com.pm.bankcards.dto.card;

import com.pm.bankcards.entity.CardStatus;

import java.math.BigDecimal;

public record CardResponse(
        Long id,
        String maskedNumber,
        String ownerUsername,
        int expiryMonth,
        int expiryYear,
        CardStatus status,
        BigDecimal balance
) {}
