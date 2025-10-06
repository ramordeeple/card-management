package com.pm.bankcards.exception;

public enum ErrorCodes {

    /// Auth
    INVALID_CREDENTIALS,
    USER_DISABLED,
    USERNAME_TAKEN,

    /// Cards
    CARD_NOT_FOUND_OR_NOT_OWNED,
    CARD_NOT_ACTIVE,
    CARD_ALREADY_BLOCKED,

    /// Transfers
    SAME_CARD,
    AMOUNT_INVALID,
    INSUFFICIENT_FUNDS,

    /// Other
    UNKNOWN_ERROR
}
