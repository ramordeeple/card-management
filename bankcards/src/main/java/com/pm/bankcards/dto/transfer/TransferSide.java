package com.pm.bankcards.dto.transfer;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Сторона перевода")
public enum TransferSide {
    FROM,
    TO;
}
