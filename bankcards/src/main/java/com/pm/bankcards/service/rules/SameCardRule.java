package com.pm.bankcards.service.rules;

import com.pm.bankcards.exception.BusinessException;
import com.pm.bankcards.exception.ErrorCodes;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SameCardRule implements TransferRule {

    @Override
    public void check(TransferContext ctx) {
        if (ctx.from().getId().equals(ctx.to().getId())) {
            throw new BusinessException(
                    ErrorCodes.SAME_CARD,
                    "Transferring funds to the same card is not allowed",
                    Map.of("cardId", ctx.from().getId())
            );
        }
    }
}
