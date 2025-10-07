package com.pm.bankcards.service.rules;

import com.pm.bankcards.exception.BusinessException;
import com.pm.bankcards.exception.ErrorCodes;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SufficientFundsRule implements TransferRule {
    @Override
    public void check(TransferContext ctx) {
        if (ctx.from().getBalance().compareTo(ctx.amount()) < 0) {
            throw new BusinessException(
                    ErrorCodes.INSUFFICIENT_FUNDS,
                    "Insufficient Funds for transfer",
                    Map.of("cardId", ctx.from().getId(),
                            "available", ctx.from().getBalance(),
                            "requestedAmount", ctx.amount())
            );
        }
    }
}
