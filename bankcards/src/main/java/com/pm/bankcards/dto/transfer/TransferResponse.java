package com.pm.bankcards.dto.transfer;


import java.math.BigDecimal;
import java.time.Instant;

public record TransferResponse(
    Long id,
    String fromMasked,
    String toMasked,
    BigDecimal amount,
    Instant createdAt
) {}
