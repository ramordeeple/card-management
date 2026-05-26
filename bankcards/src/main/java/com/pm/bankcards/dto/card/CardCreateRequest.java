package com.pm.bankcards.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record CardCreateRequest(
        @Schema(example = "4111111111114242",
        description = "Complete card number")
        @NotBlank
        @Pattern(regexp = "\\d{16}", message = "Card should contain 16 digis")
        String number,

        @Schema(example = "12", description = "Expiration month (1-12)")
        @Min(value = 1)
        @Max(value = 12)
        int expiryMonth,

        @Schema(example = "2030", description = "Expiration year")
        @Min(value = 2024)
        int expiryYear,

        @Schema(example = "2", description = "ID owner (user)")
        @NotNull
        Long ownerId
) {}
