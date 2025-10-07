package com.pm.bankcards.service.rules;

import com.pm.bankcards.exception.BusinessException;
import com.pm.bankcards.exception.ErrorCodes;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AmountPositiveRule implements TransferRule {

    @Override
    public void check(TransferContext ctx) {
        if (ctx.amount() == null || ctx.amount().signum() <= 0) {
            throw new BusinessException(
                    ErrorCodes.AMOUNT_INVALID,
                    "Transfer amount should be positive",
                    Map.of("amount", ctx.amount())
            );
        }
    }
}
