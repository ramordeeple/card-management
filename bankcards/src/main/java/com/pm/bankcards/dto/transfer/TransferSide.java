package com.pm.bankcards.dto.transfer;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Transfer side")
public enum TransferSide {
    FROM,
    TO;
}
