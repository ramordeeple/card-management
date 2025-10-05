package com.pm.bankcards.dto.card;

import jakarta.validation.constraints.*;

public record CardCreateRequest(
   @NotBlank
   @Pattern(regexp = "\\d{16}", message = "Card number should contain 16 digits")
   String number,

   @Min(value = 1)
   @Max(value = 12)
   int expiryMonth,

   @Min(value = 2024)
   int expiryYear,

   @NotNull
   Long ownerId
) {}
