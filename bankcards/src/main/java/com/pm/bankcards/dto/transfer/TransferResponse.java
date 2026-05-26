package com.pm.bankcards.dto.transfer;


import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Результат перевода")
public record TransferResponse(
    @Schema(example = "777", description = "Transfer operation ID")
    Long id,
    @Schema(example = "**** **** **** 1111", description = "Sender's card (masked)")
    String fromMasked,
    @Schema(example = "**** **** **** 1111", description = "Receiver's card (masked)")
    String toMasked,
    @Schema(example = "100.00", description = "Transfer amount")
    BigDecimal amount,
    @Schema(example = "2025-10-07T15:42:00Z", description = "Transfer completion time")
    Instant createdAt
) {}
