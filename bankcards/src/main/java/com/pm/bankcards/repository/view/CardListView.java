package com.pm.bankcards.repository.view;

import com.pm.bankcards.entity.CardStatus;

import java.math.BigDecimal;

public interface CardListView {
    Long getId();
    String getLast4();
    int getExpiryMonth();
    int getExpiryYear();
    CardStatus getStatus();
    BigDecimal getBalance();
}
