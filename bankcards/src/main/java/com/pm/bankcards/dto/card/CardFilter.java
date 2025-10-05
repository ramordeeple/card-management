package com.pm.bankcards.dto.card;

import com.pm.bankcards.entity.CardStatus;

public record CardFilter(
        String last4,
        CardStatus status,
        Integer expiryYear
) {}
