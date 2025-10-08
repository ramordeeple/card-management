package com.pm.bankcards.dto.transfer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransferRequest(
        @Schema(example = "1", description = "ID карты отправителя")
        @NotNull
        Long fromCardId,

        @Schema(example = "2", description = "ID карты получателя")
        @NotNull
        Long toCardId,

        @Schema(example = "100.00", description = "Сумма перевода")
        @DecimalMin(value = "0.01", message = "Сумма перевода должна быть положительной")
        BigDecimal amount,

        @Schema(example = "req-12345", description = "Идемпотентный ID запроса")
        @NotBlank String requestId
) {}
