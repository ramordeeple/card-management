package com.pm.bankcards.repository.view;

import java.math.BigDecimal;

public interface BalanceView {
    Long getId();
    BigDecimal getBalance(Long cardId);
}
