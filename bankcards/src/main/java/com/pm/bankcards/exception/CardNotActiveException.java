package com.pm.bankcards.exception;

import com.pm.bankcards.dto.transfer.TransferSide;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class CardNotActiveException extends BusinessException {
    public CardNotActiveException(Long cardId, TransferSide side, String status) {
        super(
                ErrorCodes.CARD_NOT_ACTIVE,
                "Card is inactive and cannot be used for transfers",
                Map.of("cardId", cardId, "side", side, "status", status),
                HttpStatus.CONFLICT
        );
    }
}
