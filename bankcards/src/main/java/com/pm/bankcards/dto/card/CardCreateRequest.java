package com.pm.bankcards.dto.card;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record CardCreateRequest(
        @Schema(example = "4111111111114242",
        description = "Полный номер карты")
        @NotBlank
        @Pattern(regexp = "\\d{16}", message = "Card number should contain 16 digits")
        String number,

        @Schema(example = "12", description = "Месяц истечения срока карты(1-12)")
        @Min(value = 1)
        @Max(value = 12)
        int expiryMonth,

        @Schema(example = "2030", description = "Год истечения срока карты")
        @Min(value = 2024)
        int expiryYear,

        @Schema(example = "2", description = "ID владельца (пользователя)")
        @NotNull
        Long ownerId
) {}
