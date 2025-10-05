package com.pm.bankcards.dto.card;


import java.math.BigDecimal;

public record BalanceResponse(
        Long id,
        BigDecimal balance
) {}
