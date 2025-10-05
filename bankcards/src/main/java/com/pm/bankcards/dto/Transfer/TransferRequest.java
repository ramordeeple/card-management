package com.pm.bankcards.dto.Transfer;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransferRequest(
        @NotNull Long fromCardId,
        @NotNull Long toCardId,
        @DecimalMin(value = "0.01", message = "Amount of transfer should be greater than 0")
        BigDecimal amount,
        @NotBlank String requestId

) {}
