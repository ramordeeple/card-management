package com.pm.bankcards.dto.card;

import com.pm.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Информация о банковской карте пользователя")
public record CardResponse(
        @Schema(example = "42", description = "ID карты")
        Long id,
        @Schema(example = "**** **** **** 4242", description = "Маскированный номер карты")
        String maskedNumber,
        @Schema(example = "Alice", description = "Имя владельца карты")
        String ownerUsername,
        @Schema(example = "12", description = "Месяц истечения срока действия карты (1-12)")
        int expiryMonth,
        @Schema(example = "2030", description = "Год истечения срока действия карты")
        int expiryYear,
        @Schema(example = "ACTIVE", description = "Статус карты")
        CardStatus status,
        @Schema(example = "15420.50", description = "Текущий баланс")
        BigDecimal balance
) {}
