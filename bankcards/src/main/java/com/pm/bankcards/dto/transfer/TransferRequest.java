package com.pm.bankcards.dto.transfer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransferRequest(
        @Schema(example = "1", description = "Sender's card ID")
        @NotNull
        Long fromCardId,

        @Schema(example = "2", description = "Receiver's card ID")
        @NotNull
        Long toCardId,

        @Schema(example = "100.00", description = "Transfer amount")
        @DecimalMin(value = "0.01", message = "Transfer amount should be positive")
        BigDecimal amount,

        @Schema(example = "req-12345", description = "Idempotent request ID")
        @NotBlank String requestId
) {}
