package com.pm.bankcards.controller;

import com.pm.bankcards.dto.card.BalanceResponse;
import com.pm.bankcards.dto.card.CardFilter;
import com.pm.bankcards.dto.card.CardResponse;
import com.pm.bankcards.security.AuthUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pm.bankcards.service.api.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardQueryService cards;

    public CardController(CardQueryService cards) {
        this.cards = cards;
    }

    @GetMapping
    public Page<CardResponse> findMycards(
            @AuthenticationPrincipal AuthUser me,
            CardFilter filter,
            Pageable pageable
            ) {
        return cards.findMyCards(me.getId(), pageable, filter);
    }

    @GetMapping("/{id}/balance")
    public BalanceResponse balance(
            @AuthenticationPrincipal AuthUser me,
            @PathVariable Long id
    ) {
        BigDecimal balance = cards.getBalance(me.getId(), id);
        return new BalanceResponse(me.getId(), balance);
    }

    public void requestBlock(
            @AuthenticationPrincipal AuthUser me,
            @PathVariable Long id
    ) {
        cards.requestBlock(me.getId(), id);
    }
}
