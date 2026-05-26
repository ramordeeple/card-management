package com.pm.bankcards.dto.card;


import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Card balance")
public record BalanceResponse(
        @Schema(example = "42", description = "Card ID")
        Long id,
        @Schema(example = "15420.50", description = "Card balance")
        BigDecimal balance
) {}
