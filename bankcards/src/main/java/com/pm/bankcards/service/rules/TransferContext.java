package com.pm.bankcards.service.rules;

import com.pm.bankcards.entity.Card;

import java.math.BigDecimal;

public record TransferContext(
        Card from,
        Card to,
        BigDecimal amount,
        String requestId
) {}
