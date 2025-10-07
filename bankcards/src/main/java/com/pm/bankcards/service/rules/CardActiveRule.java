package com.pm.bankcards.service.rules;

import com.pm.bankcards.dto.transfer.TransferSide;
import com.pm.bankcards.entity.CardStatus;
import com.pm.bankcards.exception.CardNotActiveException;
import org.springframework.stereotype.Component;
import com.pm.bankcards.entity.Card;

@Component
public class CardActiveRule implements TransferRule {

    @Override
    public void check(TransferContext ctx) {
        validateActive(ctx.from(), TransferSide.FROM);
        validateActive(ctx.to(), TransferSide.TO);
    }

    private void validateActive(Card card, TransferSide side) {
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new CardNotActiveException(
                    card.getId(),
                    side,
                    card.getStatus().name()
            );
        }
    }
}
