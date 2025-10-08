package com.pm.bankcards.dto.card;

import com.pm.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Фильтры поиска карт")
public record CardFilter(
        @Schema(example = "4242", description = "Последние 4 цифры карты")
        String last4,
        @Schema(example = "ACTIVE", description = "Статус карты")
        CardStatus status,
        @Schema(example = "2030", description = "Год истечения срока карты")
        Integer expiryYear
) {}
