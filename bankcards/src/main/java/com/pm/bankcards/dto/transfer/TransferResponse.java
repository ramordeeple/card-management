package com.pm.bankcards.dto.transfer;


import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Результат перевода")
public record TransferResponse(
    @Schema(example = "777", description = "ID операции перевода")
    Long id,
    @Schema(example = "**** **** **** 1111", description = "Карта отправителя (маска)")
    String fromMasked,
    @Schema(example = "**** **** **** 1111", description = "Карта получателя (маска)")
    String toMasked,
    @Schema(example = "100.00", description = "Сумма перевода")
    BigDecimal amount,
    @Schema(example = "2025-10-07T15:42:00Z", description = "Время выполнения перевода")
    Instant createdAt
) {}
