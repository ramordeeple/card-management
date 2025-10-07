package com.pm.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {

        super(
                ErrorCodes.CARD_NOT_FOUND_OR_NOT_OWNED,
                message,
                null,
                HttpStatus.NOT_FOUND);
    }
}
