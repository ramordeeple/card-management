package com.pm.bankcards.dto.card;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TopUpRequest(
        @NotNull Long cardId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotBlank String requestId
) {}
