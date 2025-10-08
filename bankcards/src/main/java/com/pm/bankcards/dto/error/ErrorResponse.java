package com.pm.bankcards.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Стандартная ошибка API")
public record ErrorResponse(
        @Schema(example = "2025-10-07T12:34:56Z")
        String timestamp,

        @Schema(example = "INVALID_CREDENTIALS")
        String code,

        @Schema(example = "Неверные учетные данные")
        String message, Map<String, Object> details
) { }
