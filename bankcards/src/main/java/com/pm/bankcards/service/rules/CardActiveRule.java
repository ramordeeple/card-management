package com.pm.bankcards.service.rules;

import com.pm.bankcards.dto.transfer.TransferSide;
import com.pm.bankcards.entity.CardStatus;
import com.pm.bankcards.exception.CardNotActiveException;
import org.springframework.stereotype.Component;

@Component
public class CardActiveRule implements TransferRule {

    @Override
    public void check(TransferContext ctx) {
        if (ctx.from().getStatus() != CardStatus.ACTIVE)
            throw new CardNotActiveException(
                    ctx.from().getId(),
                    TransferSide.FROM,
                    ctx.from().getStatus().name()
            );

        if (ctx.to().getStatus() != CardStatus.ACTIVE)
            throw new CardNotActiveException(
                    ctx.to().getId(),
                    TransferSide.TO,
                    ctx.to().getStatus().name()
            );
    }
}
