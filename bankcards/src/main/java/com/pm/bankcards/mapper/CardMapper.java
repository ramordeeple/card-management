package com.pm.bankcards.mapper;

import com.pm.bankcards.dto.card.CardResponse;
import com.pm.bankcards.entity.Card;
import com.pm.bankcards.util.CardMasker;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {
    public CardResponse toDto(Card c) {
        if (c == null) return null;
        return new CardResponse(
                c.getId(),
                CardMasker.mask(c.getLast4()),
                c.getOwner().getUsername(),
                c.getExpiryMonth(),
                c.getExpiryYear(),
                c.getStatus(),
                c.getBalance()
        );
    }
}
