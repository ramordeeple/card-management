package com.pm.bankcards.dto.card;


import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Баланс карты")
public record BalanceResponse(
        @Schema(example = "42", description = "ID карты")
        Long id,
        @Schema(example = "15420.50", description = "Баланс карты")
        BigDecimal balance
) {}
